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
package db.common;

import db.interfaces.RepositoryFactory;

/**
 * @author stephan
 */
public abstract class DBManager implements RepositoryFactory
{
  private static final String DRIVER_POSTGRES = "org.postgresql.Driver";
  private static final String DRIVER_MONGO = "mongo";
  private static DBManager instance;

  public static DBManager get(String driver, String url, String username, String password)
  {
    switch(driver)
    {
      case DRIVER_POSTGRES:
        if(instance == null) instance = new DBManagerPostgres(driver, url, username, password);
        break;
      case DRIVER_MONGO:
        if(instance == null) instance = new DBManagerMongo(url, username, password);
        break;
      default:
        throw new UnsupportedOperationException("Not yet implemented");
    }
    return instance;
  }
}
