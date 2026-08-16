package com.henrisusanto.rentipro.core.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.henrisusanto.rentipro.core.database.dao.RentalDao;
import com.henrisusanto.rentipro.core.database.dao.RentalDao_Impl;
import com.henrisusanto.rentipro.core.database.dao.RentalExtensionDao;
import com.henrisusanto.rentipro.core.database.dao.RentalExtensionDao_Impl;
import com.henrisusanto.rentipro.core.database.dao.RentalPresetDao;
import com.henrisusanto.rentipro.core.database.dao.RentalPresetDao_Impl;
import com.henrisusanto.rentipro.core.database.dao.RentalUnitDao;
import com.henrisusanto.rentipro.core.database.dao.RentalUnitDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class RentiproDatabase_Impl extends RentiproDatabase {
  private volatile RentalUnitDao _rentalUnitDao;

  private volatile RentalPresetDao _rentalPresetDao;

  private volatile RentalDao _rentalDao;

  private volatile RentalExtensionDao _rentalExtensionDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `rental_units` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `status` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `rental_presets` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `durationMinutes` INTEGER NOT NULL, `price` INTEGER NOT NULL, `sortOrder` INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_rental_presets_sortOrder` ON `rental_presets` (`sortOrder`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `rentals` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `unitId` INTEGER NOT NULL, `presetId` INTEGER, `durationMinutes` INTEGER NOT NULL, `price` INTEGER NOT NULL, `startedAt` INTEGER NOT NULL, `scheduledEndAt` INTEGER NOT NULL, `returnedAt` INTEGER, `status` TEXT NOT NULL, `isPaused` INTEGER NOT NULL, `pausedAt` INTEGER, `dueSoonNotified` INTEGER NOT NULL, `overdueNotified` INTEGER NOT NULL, FOREIGN KEY(`unitId`) REFERENCES `rental_units`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_rentals_unitId` ON `rentals` (`unitId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_rentals_status` ON `rentals` (`status`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_rentals_scheduledEndAt` ON `rentals` (`scheduledEndAt`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `rental_extensions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `rentalId` INTEGER NOT NULL, `presetId` INTEGER, `addedDurationMinutes` INTEGER NOT NULL, `addedPrice` INTEGER NOT NULL, `extendedAt` INTEGER NOT NULL, FOREIGN KEY(`rentalId`) REFERENCES `rentals`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_rental_extensions_rentalId` ON `rental_extensions` (`rentalId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '78127b0333fdb8ce75b780991f41e666')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `rental_units`");
        db.execSQL("DROP TABLE IF EXISTS `rental_presets`");
        db.execSQL("DROP TABLE IF EXISTS `rentals`");
        db.execSQL("DROP TABLE IF EXISTS `rental_extensions`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsRentalUnits = new HashMap<String, TableInfo.Column>(5);
        _columnsRentalUnits.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRentalUnits.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRentalUnits.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRentalUnits.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRentalUnits.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRentalUnits = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRentalUnits = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRentalUnits = new TableInfo("rental_units", _columnsRentalUnits, _foreignKeysRentalUnits, _indicesRentalUnits);
        final TableInfo _existingRentalUnits = TableInfo.read(db, "rental_units");
        if (!_infoRentalUnits.equals(_existingRentalUnits)) {
          return new RoomOpenHelper.ValidationResult(false, "rental_units(com.henrisusanto.rentipro.core.database.entity.RentalUnitEntity).\n"
                  + " Expected:\n" + _infoRentalUnits + "\n"
                  + " Found:\n" + _existingRentalUnits);
        }
        final HashMap<String, TableInfo.Column> _columnsRentalPresets = new HashMap<String, TableInfo.Column>(4);
        _columnsRentalPresets.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRentalPresets.put("durationMinutes", new TableInfo.Column("durationMinutes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRentalPresets.put("price", new TableInfo.Column("price", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRentalPresets.put("sortOrder", new TableInfo.Column("sortOrder", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRentalPresets = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRentalPresets = new HashSet<TableInfo.Index>(1);
        _indicesRentalPresets.add(new TableInfo.Index("index_rental_presets_sortOrder", false, Arrays.asList("sortOrder"), Arrays.asList("ASC")));
        final TableInfo _infoRentalPresets = new TableInfo("rental_presets", _columnsRentalPresets, _foreignKeysRentalPresets, _indicesRentalPresets);
        final TableInfo _existingRentalPresets = TableInfo.read(db, "rental_presets");
        if (!_infoRentalPresets.equals(_existingRentalPresets)) {
          return new RoomOpenHelper.ValidationResult(false, "rental_presets(com.henrisusanto.rentipro.core.database.entity.RentalPresetEntity).\n"
                  + " Expected:\n" + _infoRentalPresets + "\n"
                  + " Found:\n" + _existingRentalPresets);
        }
        final HashMap<String, TableInfo.Column> _columnsRentals = new HashMap<String, TableInfo.Column>(13);
        _columnsRentals.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRentals.put("unitId", new TableInfo.Column("unitId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRentals.put("presetId", new TableInfo.Column("presetId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRentals.put("durationMinutes", new TableInfo.Column("durationMinutes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRentals.put("price", new TableInfo.Column("price", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRentals.put("startedAt", new TableInfo.Column("startedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRentals.put("scheduledEndAt", new TableInfo.Column("scheduledEndAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRentals.put("returnedAt", new TableInfo.Column("returnedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRentals.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRentals.put("isPaused", new TableInfo.Column("isPaused", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRentals.put("pausedAt", new TableInfo.Column("pausedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRentals.put("dueSoonNotified", new TableInfo.Column("dueSoonNotified", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRentals.put("overdueNotified", new TableInfo.Column("overdueNotified", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRentals = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysRentals.add(new TableInfo.ForeignKey("rental_units", "RESTRICT", "NO ACTION", Arrays.asList("unitId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesRentals = new HashSet<TableInfo.Index>(3);
        _indicesRentals.add(new TableInfo.Index("index_rentals_unitId", false, Arrays.asList("unitId"), Arrays.asList("ASC")));
        _indicesRentals.add(new TableInfo.Index("index_rentals_status", false, Arrays.asList("status"), Arrays.asList("ASC")));
        _indicesRentals.add(new TableInfo.Index("index_rentals_scheduledEndAt", false, Arrays.asList("scheduledEndAt"), Arrays.asList("ASC")));
        final TableInfo _infoRentals = new TableInfo("rentals", _columnsRentals, _foreignKeysRentals, _indicesRentals);
        final TableInfo _existingRentals = TableInfo.read(db, "rentals");
        if (!_infoRentals.equals(_existingRentals)) {
          return new RoomOpenHelper.ValidationResult(false, "rentals(com.henrisusanto.rentipro.core.database.entity.RentalEntity).\n"
                  + " Expected:\n" + _infoRentals + "\n"
                  + " Found:\n" + _existingRentals);
        }
        final HashMap<String, TableInfo.Column> _columnsRentalExtensions = new HashMap<String, TableInfo.Column>(6);
        _columnsRentalExtensions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRentalExtensions.put("rentalId", new TableInfo.Column("rentalId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRentalExtensions.put("presetId", new TableInfo.Column("presetId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRentalExtensions.put("addedDurationMinutes", new TableInfo.Column("addedDurationMinutes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRentalExtensions.put("addedPrice", new TableInfo.Column("addedPrice", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRentalExtensions.put("extendedAt", new TableInfo.Column("extendedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRentalExtensions = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysRentalExtensions.add(new TableInfo.ForeignKey("rentals", "CASCADE", "NO ACTION", Arrays.asList("rentalId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesRentalExtensions = new HashSet<TableInfo.Index>(1);
        _indicesRentalExtensions.add(new TableInfo.Index("index_rental_extensions_rentalId", false, Arrays.asList("rentalId"), Arrays.asList("ASC")));
        final TableInfo _infoRentalExtensions = new TableInfo("rental_extensions", _columnsRentalExtensions, _foreignKeysRentalExtensions, _indicesRentalExtensions);
        final TableInfo _existingRentalExtensions = TableInfo.read(db, "rental_extensions");
        if (!_infoRentalExtensions.equals(_existingRentalExtensions)) {
          return new RoomOpenHelper.ValidationResult(false, "rental_extensions(com.henrisusanto.rentipro.core.database.entity.RentalExtensionEntity).\n"
                  + " Expected:\n" + _infoRentalExtensions + "\n"
                  + " Found:\n" + _existingRentalExtensions);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "78127b0333fdb8ce75b780991f41e666", "7b01804c5af055c07957aebc412ca21e");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "rental_units","rental_presets","rentals","rental_extensions");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `rental_units`");
      _db.execSQL("DELETE FROM `rental_presets`");
      _db.execSQL("DELETE FROM `rentals`");
      _db.execSQL("DELETE FROM `rental_extensions`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(RentalUnitDao.class, RentalUnitDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RentalPresetDao.class, RentalPresetDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RentalDao.class, RentalDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RentalExtensionDao.class, RentalExtensionDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public RentalUnitDao rentalUnitDao() {
    if (_rentalUnitDao != null) {
      return _rentalUnitDao;
    } else {
      synchronized(this) {
        if(_rentalUnitDao == null) {
          _rentalUnitDao = new RentalUnitDao_Impl(this);
        }
        return _rentalUnitDao;
      }
    }
  }

  @Override
  public RentalPresetDao rentalPresetDao() {
    if (_rentalPresetDao != null) {
      return _rentalPresetDao;
    } else {
      synchronized(this) {
        if(_rentalPresetDao == null) {
          _rentalPresetDao = new RentalPresetDao_Impl(this);
        }
        return _rentalPresetDao;
      }
    }
  }

  @Override
  public RentalDao rentalDao() {
    if (_rentalDao != null) {
      return _rentalDao;
    } else {
      synchronized(this) {
        if(_rentalDao == null) {
          _rentalDao = new RentalDao_Impl(this);
        }
        return _rentalDao;
      }
    }
  }

  @Override
  public RentalExtensionDao rentalExtensionDao() {
    if (_rentalExtensionDao != null) {
      return _rentalExtensionDao;
    } else {
      synchronized(this) {
        if(_rentalExtensionDao == null) {
          _rentalExtensionDao = new RentalExtensionDao_Impl(this);
        }
        return _rentalExtensionDao;
      }
    }
  }
}
