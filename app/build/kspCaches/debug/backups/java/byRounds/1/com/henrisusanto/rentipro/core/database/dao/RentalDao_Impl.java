package com.henrisusanto.rentipro.core.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.RelationUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.henrisusanto.rentipro.core.database.Converters;
import com.henrisusanto.rentipro.core.database.entity.RentalEntity;
import com.henrisusanto.rentipro.core.database.entity.RentalExtensionEntity;
import com.henrisusanto.rentipro.core.database.entity.RentalUnitEntity;
import com.henrisusanto.rentipro.core.database.model.ActiveRentalWithUnit;
import com.henrisusanto.rentipro.core.database.model.HistoryRentalWithDetails;
import com.henrisusanto.rentipro.core.model.RentalStatus;
import com.henrisusanto.rentipro.core.model.UnitStatus;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class RentalDao_Impl implements RentalDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RentalEntity> __insertionAdapterOfRentalEntity;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<RentalEntity> __updateAdapterOfRentalEntity;

  private final SharedSQLiteStatement __preparedStmtOfFinalizeRental;

  public RentalDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRentalEntity = new EntityInsertionAdapter<RentalEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `rentals` (`id`,`unitId`,`presetId`,`durationMinutes`,`price`,`startedAt`,`scheduledEndAt`,`returnedAt`,`status`,`isPaused`,`pausedAt`,`dueSoonNotified`,`overdueNotified`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RentalEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getUnitId());
        if (entity.getPresetId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getPresetId());
        }
        statement.bindLong(4, entity.getDurationMinutes());
        statement.bindLong(5, entity.getPrice());
        statement.bindLong(6, entity.getStartedAt());
        statement.bindLong(7, entity.getScheduledEndAt());
        if (entity.getReturnedAt() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getReturnedAt());
        }
        final String _tmp = __converters.fromRentalStatus(entity.getStatus());
        statement.bindString(9, _tmp);
        final int _tmp_1 = entity.isPaused() ? 1 : 0;
        statement.bindLong(10, _tmp_1);
        if (entity.getPausedAt() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getPausedAt());
        }
        final int _tmp_2 = entity.getDueSoonNotified() ? 1 : 0;
        statement.bindLong(12, _tmp_2);
        final int _tmp_3 = entity.getOverdueNotified() ? 1 : 0;
        statement.bindLong(13, _tmp_3);
      }
    };
    this.__updateAdapterOfRentalEntity = new EntityDeletionOrUpdateAdapter<RentalEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `rentals` SET `id` = ?,`unitId` = ?,`presetId` = ?,`durationMinutes` = ?,`price` = ?,`startedAt` = ?,`scheduledEndAt` = ?,`returnedAt` = ?,`status` = ?,`isPaused` = ?,`pausedAt` = ?,`dueSoonNotified` = ?,`overdueNotified` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RentalEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getUnitId());
        if (entity.getPresetId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getPresetId());
        }
        statement.bindLong(4, entity.getDurationMinutes());
        statement.bindLong(5, entity.getPrice());
        statement.bindLong(6, entity.getStartedAt());
        statement.bindLong(7, entity.getScheduledEndAt());
        if (entity.getReturnedAt() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getReturnedAt());
        }
        final String _tmp = __converters.fromRentalStatus(entity.getStatus());
        statement.bindString(9, _tmp);
        final int _tmp_1 = entity.isPaused() ? 1 : 0;
        statement.bindLong(10, _tmp_1);
        if (entity.getPausedAt() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getPausedAt());
        }
        final int _tmp_2 = entity.getDueSoonNotified() ? 1 : 0;
        statement.bindLong(12, _tmp_2);
        final int _tmp_3 = entity.getOverdueNotified() ? 1 : 0;
        statement.bindLong(13, _tmp_3);
        statement.bindLong(14, entity.getId());
      }
    };
    this.__preparedStmtOfFinalizeRental = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE rentals SET status = ?, returnedAt = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final RentalEntity rental, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfRentalEntity.insertAndReturnId(rental);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final RentalEntity rental, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfRentalEntity.handle(rental);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object finalizeRental(final long id, final RentalStatus status, final long returnedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfFinalizeRental.acquire();
        int _argIndex = 1;
        final String _tmp = __converters.fromRentalStatus(status);
        _stmt.bindString(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, returnedAt);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfFinalizeRental.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<RentalEntity>> observeActive() {
    final String _sql = "SELECT * FROM rentals WHERE status = 'ACTIVE' ORDER BY scheduledEndAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"rentals"}, new Callable<List<RentalEntity>>() {
      @Override
      @NonNull
      public List<RentalEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUnitId = CursorUtil.getColumnIndexOrThrow(_cursor, "unitId");
          final int _cursorIndexOfPresetId = CursorUtil.getColumnIndexOrThrow(_cursor, "presetId");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "price");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfScheduledEndAt = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledEndAt");
          final int _cursorIndexOfReturnedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "returnedAt");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIsPaused = CursorUtil.getColumnIndexOrThrow(_cursor, "isPaused");
          final int _cursorIndexOfPausedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "pausedAt");
          final int _cursorIndexOfDueSoonNotified = CursorUtil.getColumnIndexOrThrow(_cursor, "dueSoonNotified");
          final int _cursorIndexOfOverdueNotified = CursorUtil.getColumnIndexOrThrow(_cursor, "overdueNotified");
          final List<RentalEntity> _result = new ArrayList<RentalEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RentalEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpUnitId;
            _tmpUnitId = _cursor.getLong(_cursorIndexOfUnitId);
            final Long _tmpPresetId;
            if (_cursor.isNull(_cursorIndexOfPresetId)) {
              _tmpPresetId = null;
            } else {
              _tmpPresetId = _cursor.getLong(_cursorIndexOfPresetId);
            }
            final int _tmpDurationMinutes;
            _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
            final int _tmpPrice;
            _tmpPrice = _cursor.getInt(_cursorIndexOfPrice);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final long _tmpScheduledEndAt;
            _tmpScheduledEndAt = _cursor.getLong(_cursorIndexOfScheduledEndAt);
            final Long _tmpReturnedAt;
            if (_cursor.isNull(_cursorIndexOfReturnedAt)) {
              _tmpReturnedAt = null;
            } else {
              _tmpReturnedAt = _cursor.getLong(_cursorIndexOfReturnedAt);
            }
            final RentalStatus _tmpStatus;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toRentalStatus(_tmp);
            final boolean _tmpIsPaused;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsPaused);
            _tmpIsPaused = _tmp_1 != 0;
            final Long _tmpPausedAt;
            if (_cursor.isNull(_cursorIndexOfPausedAt)) {
              _tmpPausedAt = null;
            } else {
              _tmpPausedAt = _cursor.getLong(_cursorIndexOfPausedAt);
            }
            final boolean _tmpDueSoonNotified;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfDueSoonNotified);
            _tmpDueSoonNotified = _tmp_2 != 0;
            final boolean _tmpOverdueNotified;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfOverdueNotified);
            _tmpOverdueNotified = _tmp_3 != 0;
            _item = new RentalEntity(_tmpId,_tmpUnitId,_tmpPresetId,_tmpDurationMinutes,_tmpPrice,_tmpStartedAt,_tmpScheduledEndAt,_tmpReturnedAt,_tmpStatus,_tmpIsPaused,_tmpPausedAt,_tmpDueSoonNotified,_tmpOverdueNotified);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<ActiveRentalWithUnit>> observeActiveWithUnits() {
    final String _sql = "SELECT * FROM rentals WHERE status = 'ACTIVE' ORDER BY scheduledEndAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"rental_units",
        "rentals"}, new Callable<List<ActiveRentalWithUnit>>() {
      @Override
      @NonNull
      public List<ActiveRentalWithUnit> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfUnitId = CursorUtil.getColumnIndexOrThrow(_cursor, "unitId");
            final int _cursorIndexOfPresetId = CursorUtil.getColumnIndexOrThrow(_cursor, "presetId");
            final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
            final int _cursorIndexOfPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "price");
            final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
            final int _cursorIndexOfScheduledEndAt = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledEndAt");
            final int _cursorIndexOfReturnedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "returnedAt");
            final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
            final int _cursorIndexOfIsPaused = CursorUtil.getColumnIndexOrThrow(_cursor, "isPaused");
            final int _cursorIndexOfPausedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "pausedAt");
            final int _cursorIndexOfDueSoonNotified = CursorUtil.getColumnIndexOrThrow(_cursor, "dueSoonNotified");
            final int _cursorIndexOfOverdueNotified = CursorUtil.getColumnIndexOrThrow(_cursor, "overdueNotified");
            final LongSparseArray<RentalUnitEntity> _collectionUnit = new LongSparseArray<RentalUnitEntity>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfUnitId);
              _collectionUnit.put(_tmpKey, null);
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshiprentalUnitsAscomHenrisusantoRentiproCoreDatabaseEntityRentalUnitEntity(_collectionUnit);
            final List<ActiveRentalWithUnit> _result = new ArrayList<ActiveRentalWithUnit>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final ActiveRentalWithUnit _item;
              final RentalEntity _tmpRental;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final long _tmpUnitId;
              _tmpUnitId = _cursor.getLong(_cursorIndexOfUnitId);
              final Long _tmpPresetId;
              if (_cursor.isNull(_cursorIndexOfPresetId)) {
                _tmpPresetId = null;
              } else {
                _tmpPresetId = _cursor.getLong(_cursorIndexOfPresetId);
              }
              final int _tmpDurationMinutes;
              _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
              final int _tmpPrice;
              _tmpPrice = _cursor.getInt(_cursorIndexOfPrice);
              final long _tmpStartedAt;
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
              final long _tmpScheduledEndAt;
              _tmpScheduledEndAt = _cursor.getLong(_cursorIndexOfScheduledEndAt);
              final Long _tmpReturnedAt;
              if (_cursor.isNull(_cursorIndexOfReturnedAt)) {
                _tmpReturnedAt = null;
              } else {
                _tmpReturnedAt = _cursor.getLong(_cursorIndexOfReturnedAt);
              }
              final RentalStatus _tmpStatus;
              final String _tmp;
              _tmp = _cursor.getString(_cursorIndexOfStatus);
              _tmpStatus = __converters.toRentalStatus(_tmp);
              final boolean _tmpIsPaused;
              final int _tmp_1;
              _tmp_1 = _cursor.getInt(_cursorIndexOfIsPaused);
              _tmpIsPaused = _tmp_1 != 0;
              final Long _tmpPausedAt;
              if (_cursor.isNull(_cursorIndexOfPausedAt)) {
                _tmpPausedAt = null;
              } else {
                _tmpPausedAt = _cursor.getLong(_cursorIndexOfPausedAt);
              }
              final boolean _tmpDueSoonNotified;
              final int _tmp_2;
              _tmp_2 = _cursor.getInt(_cursorIndexOfDueSoonNotified);
              _tmpDueSoonNotified = _tmp_2 != 0;
              final boolean _tmpOverdueNotified;
              final int _tmp_3;
              _tmp_3 = _cursor.getInt(_cursorIndexOfOverdueNotified);
              _tmpOverdueNotified = _tmp_3 != 0;
              _tmpRental = new RentalEntity(_tmpId,_tmpUnitId,_tmpPresetId,_tmpDurationMinutes,_tmpPrice,_tmpStartedAt,_tmpScheduledEndAt,_tmpReturnedAt,_tmpStatus,_tmpIsPaused,_tmpPausedAt,_tmpDueSoonNotified,_tmpOverdueNotified);
              final RentalUnitEntity _tmpUnit;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfUnitId);
              _tmpUnit = _collectionUnit.get(_tmpKey_1);
              _item = new ActiveRentalWithUnit(_tmpRental,_tmpUnit);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<RentalEntity> observeById(final long id) {
    final String _sql = "SELECT * FROM rentals WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"rentals"}, new Callable<RentalEntity>() {
      @Override
      @Nullable
      public RentalEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUnitId = CursorUtil.getColumnIndexOrThrow(_cursor, "unitId");
          final int _cursorIndexOfPresetId = CursorUtil.getColumnIndexOrThrow(_cursor, "presetId");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "price");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfScheduledEndAt = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledEndAt");
          final int _cursorIndexOfReturnedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "returnedAt");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIsPaused = CursorUtil.getColumnIndexOrThrow(_cursor, "isPaused");
          final int _cursorIndexOfPausedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "pausedAt");
          final int _cursorIndexOfDueSoonNotified = CursorUtil.getColumnIndexOrThrow(_cursor, "dueSoonNotified");
          final int _cursorIndexOfOverdueNotified = CursorUtil.getColumnIndexOrThrow(_cursor, "overdueNotified");
          final RentalEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpUnitId;
            _tmpUnitId = _cursor.getLong(_cursorIndexOfUnitId);
            final Long _tmpPresetId;
            if (_cursor.isNull(_cursorIndexOfPresetId)) {
              _tmpPresetId = null;
            } else {
              _tmpPresetId = _cursor.getLong(_cursorIndexOfPresetId);
            }
            final int _tmpDurationMinutes;
            _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
            final int _tmpPrice;
            _tmpPrice = _cursor.getInt(_cursorIndexOfPrice);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final long _tmpScheduledEndAt;
            _tmpScheduledEndAt = _cursor.getLong(_cursorIndexOfScheduledEndAt);
            final Long _tmpReturnedAt;
            if (_cursor.isNull(_cursorIndexOfReturnedAt)) {
              _tmpReturnedAt = null;
            } else {
              _tmpReturnedAt = _cursor.getLong(_cursorIndexOfReturnedAt);
            }
            final RentalStatus _tmpStatus;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toRentalStatus(_tmp);
            final boolean _tmpIsPaused;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsPaused);
            _tmpIsPaused = _tmp_1 != 0;
            final Long _tmpPausedAt;
            if (_cursor.isNull(_cursorIndexOfPausedAt)) {
              _tmpPausedAt = null;
            } else {
              _tmpPausedAt = _cursor.getLong(_cursorIndexOfPausedAt);
            }
            final boolean _tmpDueSoonNotified;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfDueSoonNotified);
            _tmpDueSoonNotified = _tmp_2 != 0;
            final boolean _tmpOverdueNotified;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfOverdueNotified);
            _tmpOverdueNotified = _tmp_3 != 0;
            _result = new RentalEntity(_tmpId,_tmpUnitId,_tmpPresetId,_tmpDurationMinutes,_tmpPrice,_tmpStartedAt,_tmpScheduledEndAt,_tmpReturnedAt,_tmpStatus,_tmpIsPaused,_tmpPausedAt,_tmpDueSoonNotified,_tmpOverdueNotified);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getById(final long id, final Continuation<? super RentalEntity> $completion) {
    final String _sql = "SELECT * FROM rentals WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<RentalEntity>() {
      @Override
      @Nullable
      public RentalEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUnitId = CursorUtil.getColumnIndexOrThrow(_cursor, "unitId");
          final int _cursorIndexOfPresetId = CursorUtil.getColumnIndexOrThrow(_cursor, "presetId");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "price");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfScheduledEndAt = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledEndAt");
          final int _cursorIndexOfReturnedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "returnedAt");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIsPaused = CursorUtil.getColumnIndexOrThrow(_cursor, "isPaused");
          final int _cursorIndexOfPausedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "pausedAt");
          final int _cursorIndexOfDueSoonNotified = CursorUtil.getColumnIndexOrThrow(_cursor, "dueSoonNotified");
          final int _cursorIndexOfOverdueNotified = CursorUtil.getColumnIndexOrThrow(_cursor, "overdueNotified");
          final RentalEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpUnitId;
            _tmpUnitId = _cursor.getLong(_cursorIndexOfUnitId);
            final Long _tmpPresetId;
            if (_cursor.isNull(_cursorIndexOfPresetId)) {
              _tmpPresetId = null;
            } else {
              _tmpPresetId = _cursor.getLong(_cursorIndexOfPresetId);
            }
            final int _tmpDurationMinutes;
            _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
            final int _tmpPrice;
            _tmpPrice = _cursor.getInt(_cursorIndexOfPrice);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final long _tmpScheduledEndAt;
            _tmpScheduledEndAt = _cursor.getLong(_cursorIndexOfScheduledEndAt);
            final Long _tmpReturnedAt;
            if (_cursor.isNull(_cursorIndexOfReturnedAt)) {
              _tmpReturnedAt = null;
            } else {
              _tmpReturnedAt = _cursor.getLong(_cursorIndexOfReturnedAt);
            }
            final RentalStatus _tmpStatus;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toRentalStatus(_tmp);
            final boolean _tmpIsPaused;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsPaused);
            _tmpIsPaused = _tmp_1 != 0;
            final Long _tmpPausedAt;
            if (_cursor.isNull(_cursorIndexOfPausedAt)) {
              _tmpPausedAt = null;
            } else {
              _tmpPausedAt = _cursor.getLong(_cursorIndexOfPausedAt);
            }
            final boolean _tmpDueSoonNotified;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfDueSoonNotified);
            _tmpDueSoonNotified = _tmp_2 != 0;
            final boolean _tmpOverdueNotified;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfOverdueNotified);
            _tmpOverdueNotified = _tmp_3 != 0;
            _result = new RentalEntity(_tmpId,_tmpUnitId,_tmpPresetId,_tmpDurationMinutes,_tmpPrice,_tmpStartedAt,_tmpScheduledEndAt,_tmpReturnedAt,_tmpStatus,_tmpIsPaused,_tmpPausedAt,_tmpDueSoonNotified,_tmpOverdueNotified);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getActiveByUnitId(final long unitId,
      final Continuation<? super RentalEntity> $completion) {
    final String _sql = "SELECT * FROM rentals WHERE unitId = ? AND status = 'ACTIVE' LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, unitId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<RentalEntity>() {
      @Override
      @Nullable
      public RentalEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUnitId = CursorUtil.getColumnIndexOrThrow(_cursor, "unitId");
          final int _cursorIndexOfPresetId = CursorUtil.getColumnIndexOrThrow(_cursor, "presetId");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "price");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfScheduledEndAt = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledEndAt");
          final int _cursorIndexOfReturnedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "returnedAt");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIsPaused = CursorUtil.getColumnIndexOrThrow(_cursor, "isPaused");
          final int _cursorIndexOfPausedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "pausedAt");
          final int _cursorIndexOfDueSoonNotified = CursorUtil.getColumnIndexOrThrow(_cursor, "dueSoonNotified");
          final int _cursorIndexOfOverdueNotified = CursorUtil.getColumnIndexOrThrow(_cursor, "overdueNotified");
          final RentalEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpUnitId;
            _tmpUnitId = _cursor.getLong(_cursorIndexOfUnitId);
            final Long _tmpPresetId;
            if (_cursor.isNull(_cursorIndexOfPresetId)) {
              _tmpPresetId = null;
            } else {
              _tmpPresetId = _cursor.getLong(_cursorIndexOfPresetId);
            }
            final int _tmpDurationMinutes;
            _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
            final int _tmpPrice;
            _tmpPrice = _cursor.getInt(_cursorIndexOfPrice);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final long _tmpScheduledEndAt;
            _tmpScheduledEndAt = _cursor.getLong(_cursorIndexOfScheduledEndAt);
            final Long _tmpReturnedAt;
            if (_cursor.isNull(_cursorIndexOfReturnedAt)) {
              _tmpReturnedAt = null;
            } else {
              _tmpReturnedAt = _cursor.getLong(_cursorIndexOfReturnedAt);
            }
            final RentalStatus _tmpStatus;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toRentalStatus(_tmp);
            final boolean _tmpIsPaused;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsPaused);
            _tmpIsPaused = _tmp_1 != 0;
            final Long _tmpPausedAt;
            if (_cursor.isNull(_cursorIndexOfPausedAt)) {
              _tmpPausedAt = null;
            } else {
              _tmpPausedAt = _cursor.getLong(_cursorIndexOfPausedAt);
            }
            final boolean _tmpDueSoonNotified;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfDueSoonNotified);
            _tmpDueSoonNotified = _tmp_2 != 0;
            final boolean _tmpOverdueNotified;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfOverdueNotified);
            _tmpOverdueNotified = _tmp_3 != 0;
            _result = new RentalEntity(_tmpId,_tmpUnitId,_tmpPresetId,_tmpDurationMinutes,_tmpPrice,_tmpStartedAt,_tmpScheduledEndAt,_tmpReturnedAt,_tmpStatus,_tmpIsPaused,_tmpPausedAt,_tmpDueSoonNotified,_tmpOverdueNotified);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object countActiveByUnitId(final long unitId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM rentals WHERE unitId = ? AND status = 'ACTIVE'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, unitId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<HistoryRentalWithDetails>> observeHistory() {
    final String _sql = "\n"
            + "        SELECT * FROM rentals\n"
            + "        WHERE status IN ('COMPLETED', 'DELETED')\n"
            + "        ORDER BY COALESCE(returnedAt, startedAt) DESC\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"rental_units", "rental_extensions",
        "rentals"}, new Callable<List<HistoryRentalWithDetails>>() {
      @Override
      @NonNull
      public List<HistoryRentalWithDetails> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfUnitId = CursorUtil.getColumnIndexOrThrow(_cursor, "unitId");
            final int _cursorIndexOfPresetId = CursorUtil.getColumnIndexOrThrow(_cursor, "presetId");
            final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
            final int _cursorIndexOfPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "price");
            final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
            final int _cursorIndexOfScheduledEndAt = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledEndAt");
            final int _cursorIndexOfReturnedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "returnedAt");
            final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
            final int _cursorIndexOfIsPaused = CursorUtil.getColumnIndexOrThrow(_cursor, "isPaused");
            final int _cursorIndexOfPausedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "pausedAt");
            final int _cursorIndexOfDueSoonNotified = CursorUtil.getColumnIndexOrThrow(_cursor, "dueSoonNotified");
            final int _cursorIndexOfOverdueNotified = CursorUtil.getColumnIndexOrThrow(_cursor, "overdueNotified");
            final LongSparseArray<RentalUnitEntity> _collectionUnit = new LongSparseArray<RentalUnitEntity>();
            final LongSparseArray<ArrayList<RentalExtensionEntity>> _collectionExtensions = new LongSparseArray<ArrayList<RentalExtensionEntity>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfUnitId);
              _collectionUnit.put(_tmpKey, null);
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionExtensions.containsKey(_tmpKey_1)) {
                _collectionExtensions.put(_tmpKey_1, new ArrayList<RentalExtensionEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshiprentalUnitsAscomHenrisusantoRentiproCoreDatabaseEntityRentalUnitEntity(_collectionUnit);
            __fetchRelationshiprentalExtensionsAscomHenrisusantoRentiproCoreDatabaseEntityRentalExtensionEntity(_collectionExtensions);
            final List<HistoryRentalWithDetails> _result = new ArrayList<HistoryRentalWithDetails>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final HistoryRentalWithDetails _item;
              final RentalEntity _tmpRental;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final long _tmpUnitId;
              _tmpUnitId = _cursor.getLong(_cursorIndexOfUnitId);
              final Long _tmpPresetId;
              if (_cursor.isNull(_cursorIndexOfPresetId)) {
                _tmpPresetId = null;
              } else {
                _tmpPresetId = _cursor.getLong(_cursorIndexOfPresetId);
              }
              final int _tmpDurationMinutes;
              _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
              final int _tmpPrice;
              _tmpPrice = _cursor.getInt(_cursorIndexOfPrice);
              final long _tmpStartedAt;
              _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
              final long _tmpScheduledEndAt;
              _tmpScheduledEndAt = _cursor.getLong(_cursorIndexOfScheduledEndAt);
              final Long _tmpReturnedAt;
              if (_cursor.isNull(_cursorIndexOfReturnedAt)) {
                _tmpReturnedAt = null;
              } else {
                _tmpReturnedAt = _cursor.getLong(_cursorIndexOfReturnedAt);
              }
              final RentalStatus _tmpStatus;
              final String _tmp;
              _tmp = _cursor.getString(_cursorIndexOfStatus);
              _tmpStatus = __converters.toRentalStatus(_tmp);
              final boolean _tmpIsPaused;
              final int _tmp_1;
              _tmp_1 = _cursor.getInt(_cursorIndexOfIsPaused);
              _tmpIsPaused = _tmp_1 != 0;
              final Long _tmpPausedAt;
              if (_cursor.isNull(_cursorIndexOfPausedAt)) {
                _tmpPausedAt = null;
              } else {
                _tmpPausedAt = _cursor.getLong(_cursorIndexOfPausedAt);
              }
              final boolean _tmpDueSoonNotified;
              final int _tmp_2;
              _tmp_2 = _cursor.getInt(_cursorIndexOfDueSoonNotified);
              _tmpDueSoonNotified = _tmp_2 != 0;
              final boolean _tmpOverdueNotified;
              final int _tmp_3;
              _tmp_3 = _cursor.getInt(_cursorIndexOfOverdueNotified);
              _tmpOverdueNotified = _tmp_3 != 0;
              _tmpRental = new RentalEntity(_tmpId,_tmpUnitId,_tmpPresetId,_tmpDurationMinutes,_tmpPrice,_tmpStartedAt,_tmpScheduledEndAt,_tmpReturnedAt,_tmpStatus,_tmpIsPaused,_tmpPausedAt,_tmpDueSoonNotified,_tmpOverdueNotified);
              final RentalUnitEntity _tmpUnit;
              final long _tmpKey_2;
              _tmpKey_2 = _cursor.getLong(_cursorIndexOfUnitId);
              _tmpUnit = _collectionUnit.get(_tmpKey_2);
              final ArrayList<RentalExtensionEntity> _tmpExtensionsCollection;
              final long _tmpKey_3;
              _tmpKey_3 = _cursor.getLong(_cursorIndexOfId);
              _tmpExtensionsCollection = _collectionExtensions.get(_tmpKey_3);
              _item = new HistoryRentalWithDetails(_tmpRental,_tmpUnit,_tmpExtensionsCollection);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Integer> observeTodayCompletedCount(final long startOfDayMillis,
      final long endOfDayMillis) {
    final String _sql = "\n"
            + "        SELECT COUNT(*) FROM rentals\n"
            + "        WHERE status = 'COMPLETED'\n"
            + "        AND returnedAt IS NOT NULL\n"
            + "        AND returnedAt >= ?\n"
            + "        AND returnedAt < ?\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startOfDayMillis);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endOfDayMillis);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"rentals"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Integer> observeTodayRevenue(final long startOfDayMillis, final long endOfDayMillis) {
    final String _sql = "\n"
            + "        SELECT COALESCE(SUM(price), 0) FROM rentals\n"
            + "        WHERE status = 'COMPLETED'\n"
            + "        AND returnedAt IS NOT NULL\n"
            + "        AND returnedAt >= ?\n"
            + "        AND returnedAt < ?\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startOfDayMillis);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endOfDayMillis);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"rentals"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object countActive(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM rentals WHERE status = 'ACTIVE'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private void __fetchRelationshiprentalUnitsAscomHenrisusantoRentiproCoreDatabaseEntityRentalUnitEntity(
      @NonNull final LongSparseArray<RentalUnitEntity> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, false, (map) -> {
        __fetchRelationshiprentalUnitsAscomHenrisusantoRentiproCoreDatabaseEntityRentalUnitEntity(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `id`,`name`,`status`,`createdAt`,`updatedAt` FROM `rental_units` WHERE `id` IN (");
    final int _inputSize = _map.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (int i = 0; i < _map.size(); i++) {
      final long _item = _map.keyAt(i);
      _stmt.bindLong(_argIndex, _item);
      _argIndex++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "id");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfName = 1;
      final int _cursorIndexOfStatus = 2;
      final int _cursorIndexOfCreatedAt = 3;
      final int _cursorIndexOfUpdatedAt = 4;
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        if (_map.containsKey(_tmpKey)) {
          final RentalUnitEntity _item_1;
          final long _tmpId;
          _tmpId = _cursor.getLong(_cursorIndexOfId);
          final String _tmpName;
          _tmpName = _cursor.getString(_cursorIndexOfName);
          final UnitStatus _tmpStatus;
          final String _tmp;
          _tmp = _cursor.getString(_cursorIndexOfStatus);
          _tmpStatus = __converters.toUnitStatus(_tmp);
          final long _tmpCreatedAt;
          _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
          final long _tmpUpdatedAt;
          _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
          _item_1 = new RentalUnitEntity(_tmpId,_tmpName,_tmpStatus,_tmpCreatedAt,_tmpUpdatedAt);
          _map.put(_tmpKey, _item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }

  private void __fetchRelationshiprentalExtensionsAscomHenrisusantoRentiproCoreDatabaseEntityRentalExtensionEntity(
      @NonNull final LongSparseArray<ArrayList<RentalExtensionEntity>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (map) -> {
        __fetchRelationshiprentalExtensionsAscomHenrisusantoRentiproCoreDatabaseEntityRentalExtensionEntity(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `id`,`rentalId`,`presetId`,`addedDurationMinutes`,`addedPrice`,`extendedAt` FROM `rental_extensions` WHERE `rentalId` IN (");
    final int _inputSize = _map.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (int i = 0; i < _map.size(); i++) {
      final long _item = _map.keyAt(i);
      _stmt.bindLong(_argIndex, _item);
      _argIndex++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "rentalId");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfRentalId = 1;
      final int _cursorIndexOfPresetId = 2;
      final int _cursorIndexOfAddedDurationMinutes = 3;
      final int _cursorIndexOfAddedPrice = 4;
      final int _cursorIndexOfExtendedAt = 5;
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        final ArrayList<RentalExtensionEntity> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final RentalExtensionEntity _item_1;
          final long _tmpId;
          _tmpId = _cursor.getLong(_cursorIndexOfId);
          final long _tmpRentalId;
          _tmpRentalId = _cursor.getLong(_cursorIndexOfRentalId);
          final Long _tmpPresetId;
          if (_cursor.isNull(_cursorIndexOfPresetId)) {
            _tmpPresetId = null;
          } else {
            _tmpPresetId = _cursor.getLong(_cursorIndexOfPresetId);
          }
          final int _tmpAddedDurationMinutes;
          _tmpAddedDurationMinutes = _cursor.getInt(_cursorIndexOfAddedDurationMinutes);
          final int _tmpAddedPrice;
          _tmpAddedPrice = _cursor.getInt(_cursorIndexOfAddedPrice);
          final long _tmpExtendedAt;
          _tmpExtendedAt = _cursor.getLong(_cursorIndexOfExtendedAt);
          _item_1 = new RentalExtensionEntity(_tmpId,_tmpRentalId,_tmpPresetId,_tmpAddedDurationMinutes,_tmpAddedPrice,_tmpExtendedAt);
          _tmpRelation.add(_item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
