package datalineagebuilder.query;

import datalineagebuilder.databaseobject.ViewOrTableDefinition;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SQLServerViewDefinitionQuery {

    public List<ViewOrTableDefinition> query(JdbcTemplate jdbcTemplate, List<String> viewNames) {
        String quotedNames = viewNames.stream().map(x -> String.format("'%s'", x)).collect(Collectors.joining(", "));
        String sql = String.format("select name, definition, type_desc \n" +
                "from sys.objects o\n" +
                "join sys.sql_modules m on m.object_id = o.object_id\n" +
                "where o.type      = 'V'\n" +
                "and name in (%s)", quotedNames);

        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);

        return maps.stream().map(x -> new ViewOrTableDefinition(
                x.get("name").toString(),
                x.get("definition").toString(),
                x.get("type_desc").toString()
        )).collect(Collectors.toList());
    }
}
