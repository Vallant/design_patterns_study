package db.mongo.repository;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import data.*;
import db.common.DBManagerMongo;
import db.interfaces.*;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.*;

public class ActivityRepositoryMongo implements ActivityRepository
{
  private final DBManagerMongo manager;

  public ActivityRepositoryMongo(DBManagerMongo db)
  {
    this.manager = db;
  }

  @Override
  public Activity getByPrimaryKey(int id) throws Exception
  {
    MongoCollection<Document> coll = manager.getDb().getCollection("activity");
    FindIterable<Document> doc = coll.find(eq("id", id));
    MongoCursor<Document> cursor = doc.iterator();
    if(!cursor.hasNext())
      throw new Exception("no such record");

    return extractActivity(cursor.next());
  }

  private Activity extractActivity(Document next) throws Exception
  {
    int id = next.getInteger("id");
    int hash = next.getInteger("hash");
    int projectPhaseId = next.getInteger("project_phase_id");
    int projectId = next.getInteger("project_id");
    String userLoginName = next.getString("user_login_name");
    String description = next.getString("description");
    Instant start = next.getDate("start_time").toInstant();
    ZonedDateTime zonedStart = ZonedDateTime.ofInstant(start, ZoneId.systemDefault());
    Instant end = next.getDate("end_time").toInstant();
    ZonedDateTime zonedEnd = ZonedDateTime.ofInstant(end, ZoneId.systemDefault());
    String comments = next.getString("comment");

    ProjectPhaseRepository ppr = new ProjectPhaseRepositoryMongo(manager);
    ProjectPhase phase = ppr.getByPrimaryKey(projectPhaseId);
    UserRepository ur = new UserRepositoryMongo(manager);
    User user = ur.getByPrimaryKey(userLoginName);
    return new Activity(hash, id, phase, user, description, zonedStart, zonedEnd, comments);
  }

  @Override
  public void getParticipatingProjectsAndWorkloadSince(String loginName, ZonedDateTime since,
                                                       ArrayList<Project> projects, ArrayList<Duration> durations)
    throws Exception
  {
    MongoCollection coll = manager.getDb().getCollection("activity");
    ProjectMemberRepository pmr = new ProjectMemberRepositoryMongo(manager);
    ArrayList<ProjectMember> owned = pmr.getInvolvedProjects(loginName);
    owned.addAll(pmr.getOwnedProject(loginName));
    for(ProjectMember m : owned)
    {
      projects.add(m.getProject());
      FindIterable<Document> result = coll.find(
        and(
          eq("user_login_name", loginName),
          gte("start_time", Date.from(since.toInstant())),
          eq("project_id", m.getProjectId())
        )
      );
      Duration sum = Duration.ZERO;
      for(Document doc : result)
      {
        Date startDate = doc.getDate("start_time");
        Date endDate = doc.getDate("end_time");
        Duration duration = Duration.between(startDate.toInstant(), endDate.toInstant());
        sum = sum.plus(duration);
      }
      durations.add(sum);
    }
  }

  @Override
  public void getPhasesAndWorkloadSince(String loginName, int projectId, ZonedDateTime since,
                                        ArrayList<ProjectPhase> phases, ArrayList<Duration> durations) throws Exception
  {
    Date sinceDate = Date.from(since.toInstant());
    Bson match = and(
      eq("user_login_name", loginName),
      gt("start_time", sinceDate),
      eq("project_id", projectId)
    );

    MongoCollection coll = manager.getDb().getCollection("activity");
    FindIterable<Document> result = coll.find(match);

    ProjectPhaseRepository ppr = new ProjectPhaseRepositoryMongo(manager);

    for(Document doc : result)
    {
      int projectPhaseId = doc.getInteger("project_phase_id");
      ProjectPhase projectPhase = ppr.getByPrimaryKey(projectPhaseId);
      phases.add(projectPhase);

      Date startDate = doc.getDate("start_time");
      Date endDate = doc.getDate("end_time");
      Duration duration = Duration.between(startDate.toInstant(), endDate.toInstant());
      durations.add(duration);
    }
  }

  @Override
  public ArrayList<Activity> getActivitiesForPhaseSince(String loginName, int phaseId, ZonedDateTime since) throws Exception
  {
    ArrayList<Activity> list = new ArrayList<>();
    Date sinceDate = Date.from(since.toInstant());
    MongoCollection coll = manager.getDb().getCollection("activity");
    FindIterable<Document> result = coll.find(
      and(
        eq("user_login_name", loginName),
        gte("start_time", sinceDate),
        eq("project_phase_id", phaseId)
      )
    );

    for(Document doc : result)
      list.add(extractActivity(doc));
    return list;
  }

  @Override
  public ArrayList<Activity> getActivitiesByUserForPhaseSince(String loginName, int phaseId, ZonedDateTime since)
    throws Exception
  {
    ArrayList<Activity> list = new ArrayList<>();
    Date sinceDate = Date.from(since.toInstant());
    MongoCollection coll = manager.getDb().getCollection("activity");
    FindIterable<Document> result = coll.find(
      and(
        loginName.isEmpty() ?
          not(eq("user_login_name", ""))
          : eq("user_login_name", loginName),
        gte("start_time", sinceDate),
        eq("project_phase_id", phaseId)
      )
    );

    for(Document doc : result)
      list.add(extractActivity(doc));
    return list;
  }

  @Override
  public void getOwnedProjectsAndWorkloadSince(String loginName, ZonedDateTime since, ArrayList<Project> projects,
                                               ArrayList<Duration> durations) throws Exception
  {
    MongoCollection coll = manager.getDb().getCollection("activity");
    ProjectMemberRepository pmr = new ProjectMemberRepositoryMongo(manager);
    ArrayList<ProjectMember> owned = pmr.getOwnedProject(loginName);
    for(ProjectMember m : owned)
    {
      projects.add(m.getProject());
      FindIterable<Document> result = coll.find(
        and(
          eq("user_login_name", loginName),
          gte("start_time", Date.from(since.toInstant())),
          eq("project_id", m.getProjectId())
        )
      );
      Duration sum = Duration.ZERO;
      for(Document doc : result)
      {
        Date startDate = doc.getDate("start_time");
        Date endDate = doc.getDate("end_time");
        Duration duration = Duration.between(startDate.toInstant(), endDate.toInstant());
        sum = sum.plus(duration);
      }
      durations.add(sum);
    }
  }

  @Override
  public void getPhasesAndWorkloadForUserSince(String loginName, int projectId, ZonedDateTime since,
                                               ArrayList<ProjectPhase> phases, ArrayList<Duration> durations)
    throws Exception
  {
    Date sinceDate = Date.from(since.toInstant());
    Bson match = and(
      loginName.isEmpty() ?
        not(eq("user_login_name", ""))
        : eq("user_login_name", loginName),
      gt("start_time", sinceDate),
      eq("project_id", projectId)
    );

    MongoCollection coll = manager.getDb().getCollection("activity");
    FindIterable<Document> result = coll.find(match);

    ProjectPhaseRepository ppr = new ProjectPhaseRepositoryMongo(manager);

    for(Document doc : result)
    {
      int projectPhaseId = doc.getInteger("project_phase_id");
      ProjectPhase projectPhase = ppr.getByPrimaryKey(projectPhaseId);
      phases.add(projectPhase);

      Date startDate = doc.getDate("start_time");
      Date endDate = doc.getDate("end_time");
      Duration duration = Duration.between(startDate.toInstant(), endDate.toInstant());
      durations.add(duration);
    }
  }

  @Override
  public void add(Activity item) throws Exception
  {
    MongoCollection<Document> coll = manager.getDb().getCollection("activity");

    Document toAdd = new Document("hash", item.getLocalHash())
      .append("project_phase_id", item.getProjectPhaseId())
      .append("project_id", item.getProjectId())
      .append("user_login_name", item.getUserLoginName())
      .append("description", item.getDescription())
      .append("start_time", Date.from(item.getStart().toLocalDateTime().atZone(ZoneId.of("UTC")).toInstant()))
      .append("end_time", Date.from(item.getStop().toLocalDateTime().atZone(ZoneId.of("UTC")).toInstant()))
      .append("comment", item.getComments())
      .append("id", manager.getNextSequence("activity"));
    coll.insertOne(toAdd);
    item.setId(toAdd.getInteger("id"));
  }

  @Override
  public void update(Activity item) throws Exception
  {
    MongoCollection<Document> coll = manager.getDb().getCollection("activity");
    Document toUpdate = new Document("hash", item.getLocalHash())
      .append("project_phase_id", item.getProjectPhaseId())
      .append("project_id", item.getProjectId())
      .append("user_login_name", item.getUserLoginName())
      .append("description", item.getDescription())
      .append("start_time", Date.from(item.getStart().toLocalDateTime().atZone(ZoneId.of("UTC")).toInstant()))
      .append("end_time", Date.from(item.getStop().toLocalDateTime().atZone(ZoneId.of("UTC")).toInstant()))
      .append("comment", item.getComments())
      .append("id", item.getId());

    UpdateResult result = coll.replaceOne(
      and(
        eq("id", item.getId()),
        eq("hash", item.getRemoteHash())), toUpdate);
    if(result.getModifiedCount() != 1)
      throw new Exception("Record was modified or not found");

    item.setRemoteHash(item.getLocalHash());
  }

  @Override
  public void delete(Activity item) throws Exception
  {
    MongoCollection<Document> coll = manager.getDb().getCollection("activity");
    DeleteResult result = coll.deleteOne(and(eq("id", item.getId()), eq("hash", item.getRemoteHash())));
    if(result.getDeletedCount() != 1)
      throw new Exception("Record was modified or not found");
  }

  @Override
  public List<Activity> getAll() throws Exception
  {
    ArrayList<Activity> list = new ArrayList<>();
    MongoCollection<Document> coll = manager.getDb().getCollection("activity");
    FindIterable<Document> doc = coll.find();
    MongoCursor<Document> cursor = doc.iterator();
    while(cursor.hasNext())
      list.add(extractActivity(cursor.next()));

    return list;
  }
}
