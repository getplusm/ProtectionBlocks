package t.me.p1azmer.plugin.protectionblocks.data;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.data.AbstractUserDataHandler;
import t.me.p1azmer.engine.api.data.sql.SQLColumn;
import t.me.p1azmer.engine.api.data.sql.SQLValue;
import t.me.p1azmer.engine.api.data.sql.column.ColumnType;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.data.impl.RegionUser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;

public class DataHandler extends AbstractUserDataHandler<ProtectionPlugin, RegionUser> {

    private static final SQLColumn COLUMN_REGIONS = SQLColumn.of("regions", ColumnType.STRING);

    private static DataHandler instance;

    private final Function<ResultSet, RegionUser> userFunction;

    protected DataHandler(@NotNull ProtectionPlugin plugin) {
        super(plugin, plugin);

        this.userFunction = (resultSet) -> {
            try {
                UUID uuid = UUID.fromString(resultSet.getString(COLUMN_USER_ID.getName()));
                String name = resultSet.getString(COLUMN_USER_NAME.getName());
                long dateCreated = resultSet.getLong(COLUMN_USER_DATE_CREATED.getName());
                long lastOnline = resultSet.getLong(COLUMN_USER_LAST_ONLINE.getName());

                Map<String, String> regions = gson.fromJson(resultSet.getString(COLUMN_REGIONS.getName()), new TypeToken<Map<String, String>>() {}.getType());
                this.mapSwapper(regions);

                return new RegionUser(plugin, uuid, name, dateCreated, lastOnline, regions);
            }
            catch (SQLException exception) {
                exception.printStackTrace();
                return null;
            }
        };
    }

    private void mapSwapper(@NotNull Map<String, String> map) { // bad but easy method
        Map<String, String> updatedMap = new HashMap<>();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (plugin.getRegionManager().getRegionBlockById(key) != null) {
                updatedMap.put(value, key);
            } else {
                updatedMap.put(key, value);
            }
        }

        map.clear();
        map.putAll(updatedMap);
    }

    @NotNull
    public static synchronized DataHandler getInstance(@NotNull ProtectionPlugin plugin) {
        if (instance == null) {
            instance = new DataHandler(plugin);
        }
        return instance;
    }

    @Override
    protected void onShutdown() {
        super.onShutdown();
        instance = null;
    }

    @Override
    public void onSynchronize() {
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        this.addColumn(this.tableUsers);
    }

    @Override
    @NotNull
    protected List<SQLColumn> getExtraColumns() {
        return Collections.singletonList(COLUMN_REGIONS);
    }

    @Override
    @NotNull
    protected List<SQLValue> getSaveColumns(@NotNull RegionUser user) {
        return Collections.singletonList(
                COLUMN_REGIONS.toValue(this.gson.toJson(user.getRegions()))
        );
    }

    @Override
    @NotNull
    protected Function<ResultSet, RegionUser> getFunctionToUser() {
        return this.userFunction;
    }
}