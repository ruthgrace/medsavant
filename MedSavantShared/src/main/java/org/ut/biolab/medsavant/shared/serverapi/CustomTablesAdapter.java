/**
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.ut.biolab.medsavant.shared.serverapi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

import org.ut.biolab.medsavant.shared.db.TableSchema;
import org.ut.biolab.medsavant.shared.model.SessionExpiredException;
import org.ut.biolab.medsavant.shared.util.ClientPermission;
import static org.ut.biolab.medsavant.shared.util.ClientType.*;

/**
 *
 * @author mfiume
 */
public interface CustomTablesAdapter extends Remote {

    @ClientPermission(deny=WEB)
    public TableSchema getCustomTableSchema(String sid, String tablename) throws SQLException, RemoteException, SessionExpiredException;
    @ClientPermission(deny=WEB)
    public TableSchema getCustomTableSchema(String sid, String tablename, boolean update) throws SQLException, RemoteException, SessionExpiredException;

}
