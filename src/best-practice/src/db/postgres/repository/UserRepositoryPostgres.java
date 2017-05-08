/*
 * Copyright (C) 2017 stephan
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package db.postgres.repository;

import data.User;
import db.common.DBManager;
import db.common.DBManagerPostgres;
import db.interfaces.Criteria;
import db.interfaces.SQLCriteria;
import java.util.ArrayList;
import db.interfaces.UserRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @created $date
 * @author stephan
 */
public class UserRepositoryPostgres implements UserRepository
{

    @Override
    public void add(User item) throws Exception
    {
        assert(item != null);
        DBManagerPostgres db = (DBManagerPostgres) DBManager.getInstance();
        try(Connection con = db.getConnection())
        {
            String sql = "INSERT INTO USERS(HASH, FIRST_NAME, LAST_NAME, ROLE, "
                    + "SALT, PASSWORD, LOGIN_NAME, EMAIL) "
                    + "VALUES "
                    + "(?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            
            int index = 1;
            ps.setInt(index++, item.getLocalHash());
            ps.setString(index++, item.getFirstName());
            ps.setString(index++, item.getLastName());
            ps.setString(index++, item.getRole().name());
            ps.setBytes(index++, item.getSalt());
            ps.setBytes(index++, item.getPassword());
            ps.setString(index++, item.getLoginName());
            ps.setString(index++, item.getEmail());
            
            ps.executeUpdate();
            item.setRemoteHash(item.getLocalHash());
        }
    }

    @Override
    public void update(User item) throws Exception
    {
        assert(item != null);
        DBManagerPostgres db = (DBManagerPostgres) DBManager.getInstance();
        SQLCriteria c = (SQLCriteria) getPrimaryKeyAndHashCriteria(item);
        try(Connection con = db.getConnection())
        {
            String sql = "UPDATE USERS SET HASH = ?, FIRST_NAME = ?, LAST_NAME = ?, ROLE = ?, "
                    + "SALT = ?, PASSWORD = ?, LOGIN_NAME = ?, EMAIL = ? "
                    + "WHERE "
                    + c.toSqlClause();
            
            PreparedStatement ps = con.prepareStatement(sql);
            
            
            int index = 1;
            ps.setInt(index++, item.getLocalHash());
            ps.setString(index++, item.getFirstName());
            ps.setString(index++, item.getLastName());
            ps.setString(index++, item.getRole().name());
            ps.setBytes(index++, item.getSalt());
            ps.setBytes(index++, item.getPassword());
            ps.setString(index++, item.getLoginName());
            ps.setString(index++, item.getEmail());
            
            c.prepareStatement(ps, index);
            
            int numRowsAffected = ps.executeUpdate();
            if(numRowsAffected == 0)
                throw new Exception("Record has changed or was not found!");
        }
    }

    @Override
    public void remove(User item) throws Exception
    {
        assert(item != null);
        DBManagerPostgres db = (DBManagerPostgres) DBManager.getInstance();
        SQLCriteria c = (SQLCriteria) getPrimaryKeyAndHashCriteria(item);
        try(Connection con = db.getConnection())
        {
            String sql = "DELETE FROM USERS "
                    + "WHERE "
                    + c.toSqlClause();
            
            PreparedStatement ps = con.prepareStatement(sql);
            c.prepareStatement(ps, 1);
            
            int numRowsAffected = ps.executeUpdate();
            if(numRowsAffected == 0)
                throw new Exception("Record has changed or was not found!");
        }
    }

    @Override
    public User getByPrimaryKey(Criteria c) throws Exception
    {
        DBManager db = DBManager.getInstance();
        ArrayList<User> l = getByCriteria(c);
        if(l.isEmpty())
            throw new Exception("No such item");
        return l.get(0);
    }

    @Override
    public ArrayList<User> getByCriteria(Criteria criterias) throws Exception
    {
        ArrayList<User> l = new ArrayList<>();
        DBManagerPostgres db = (DBManagerPostgres) DBManager.getInstance();
        SQLCriteria c = (SQLCriteria) criterias;
        try(Connection con = db.getConnection())
        {
            String sql = "SELECT HASH, FIRST_NAME, LAST_NAME, ROLE, "
                    + "SALT, PASSWORD, LOGIN_NAME, EMAIL "
                    + "FROM USERS "
                    + "WHERE "
                    + c.toSqlClause();
            PreparedStatement ps = con.prepareStatement(sql);
            c.prepareStatement(ps, 1);
            
            ResultSet rs = ps.executeQuery();
            while(rs.next())
            {
                int hash = rs.getInt("HASH");
                String firstName = rs.getString("FIRST_NAME");
                String lastName = rs.getString("LAST_NAME");
                String role = rs.getString("ROLE");
                byte[] salt = rs.getBytes("SALT");
                byte[] password = rs.getBytes("PASSWORD");
                String loginName = rs.getString("LOGIN_NAME");
                String email = rs.getString("EMAIL");
                
                l.add(new User(hash, loginName, firstName, lastName, 
                        User.ROLE.valueOf(role), email, password, salt));
            }
        }
        return l;
    }

    @Override
    public Criteria getPrimaryKeyCriteria(User item)
    {
        return DBManager.getInstance().getStringCriteria("LOGIN_NAME", item.getLoginName());
    }

    @Override
    public Criteria getPrimaryKeyAndHashCriteria(User item)
    {
        Criteria left = getPrimaryKeyCriteria(item);
        Criteria right = DBManager.getInstance().getHashCriteria(item.getRemoteHash());
        return DBManager.getInstance().getAndCriteria(left, right);
    }

    @Override
    public User getByPrimaryKey(String loginName) throws Exception
    {
        return getByPrimaryKey(DBManager.getInstance().getStringCriteria("LOGIN_NAME", loginName));
    }

}