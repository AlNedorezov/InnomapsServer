package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import db.CoordinateType;
import db.EdgeType;
import db.RoomType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.clientservercommunicationclasses.sync.TypesSync;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by alnedorezov on 7/11/16.
 */

@RestController
public class TypesSyncController {
    private Application a = new Application();

    private List<Integer> getIdsFromCoordinateTypes(List<CoordinateType> list) throws ParseException {
        List<Integer> ids = new ArrayList<>();
        for (CoordinateType aList : list) {
            ids.add(aList.getId());
        }
        return ids;
    }

    private List<Integer> getIdsFromEdgeTypes(List<EdgeType> list) throws ParseException {
        List<Integer> ids = new ArrayList<>();
        for (EdgeType aList : list) {
            ids.add(aList.getId());
        }
        return ids;
    }

    private List<Integer> getIdsFromRoomTypes(List<RoomType> list) throws ParseException {
        List<Integer> ids = new ArrayList<>();
        for (RoomType aList : list) {
            ids.add(aList.getId());
        }
        return ids;
    }

    @RequestMapping("/resources/sync/types")
    public TypesSync syncTypes(@RequestParam(value = "date", defaultValue = "2015-07-26 15:00:00.0") String date) throws SQLException, ParseException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);

        System.out.println(new Date() + " Received GET request: return types synchronization data");

        Date modifiedDate;
        String modified = "modified";
        String dateFormat = "yyyy-MM-dd HH:mm:ss.S";

        QueryBuilder<CoordinateType, Integer> qbCoordinateType = a.coordinateTypeDao.queryBuilder();
        modifiedDate = new SimpleDateFormat(dateFormat).parse(date);
        qbCoordinateType.where().ge(modified, modifiedDate);
        PreparedQuery<CoordinateType> pcCoordinateType = qbCoordinateType.prepare();
        List<CoordinateType> coordinateTypes = a.coordinateTypeDao.query(pcCoordinateType);

        QueryBuilder<EdgeType, Integer> qbEdgeType = a.edgeTypeDao.queryBuilder();
        modifiedDate = new SimpleDateFormat(dateFormat).parse(date);
        qbEdgeType.where().ge(modified, modifiedDate);
        PreparedQuery<EdgeType> pcEdgeType = qbEdgeType.prepare();
        List<EdgeType> edgeTypes = a.edgeTypeDao.query(pcEdgeType);

        QueryBuilder<RoomType, Integer> qbRoomType = a.roomTypeDao.queryBuilder();
        modifiedDate = new SimpleDateFormat(dateFormat).parse(date);
        qbRoomType.where().ge(modified, modifiedDate);
        PreparedQuery<RoomType> pcRoomType = qbRoomType.prepare();
        List<RoomType> roomTypes = a.roomTypeDao.query(pcRoomType);

        connectionSource.close();
        return new TypesSync(getIdsFromCoordinateTypes(coordinateTypes), getIdsFromEdgeTypes(edgeTypes), getIdsFromRoomTypes(roomTypes));
    }
}

