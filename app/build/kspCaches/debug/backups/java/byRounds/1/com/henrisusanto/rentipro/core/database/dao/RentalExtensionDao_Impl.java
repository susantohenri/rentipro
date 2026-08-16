package com.henrisusanto.rentipro.core.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.henrisusanto.rentipro.core.database.entity.RentalExtensionEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class RentalExtensionDao_Impl implements RentalExtensionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RentalExtensionEntity> __insertionAdapterOfRentalExtensionEntity;

  public RentalExtensionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRentalExtensionEntity = new EntityInsertionAdapter<RentalExtensionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `rental_extensions` (`id`,`rentalId`,`presetId`,`addedDurationMinutes`,`addedPrice`,`extendedAt`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RentalExtensionEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getRentalId());
        if (entity.getPresetId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getPresetId());
        }
        statement.bindLong(4, entity.getAddedDurationMinutes());
        statement.bindLong(5, entity.getAddedPrice());
        statement.bindLong(6, entity.getExtendedAt());
      }
    };
  }

  @Override
  public Object insert(final RentalExtensionEntity extension,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfRentalExtensionEntity.insertAndReturnId(extension);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<RentalExtensionEntity>> observeByRentalId(final long rentalId) {
    final String _sql = "SELECT * FROM rental_extensions WHERE rentalId = ? ORDER BY extendedAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, rentalId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"rental_extensions"}, new Callable<List<RentalExtensionEntity>>() {
      @Override
      @NonNull
      public List<RentalExtensionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRentalId = CursorUtil.getColumnIndexOrThrow(_cursor, "rentalId");
          final int _cursorIndexOfPresetId = CursorUtil.getColumnIndexOrThrow(_cursor, "presetId");
          final int _cursorIndexOfAddedDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "addedDurationMinutes");
          final int _cursorIndexOfAddedPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "addedPrice");
          final int _cursorIndexOfExtendedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "extendedAt");
          final List<RentalExtensionEntity> _result = new ArrayList<RentalExtensionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RentalExtensionEntity _item;
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
            _item = new RentalExtensionEntity(_tmpId,_tmpRentalId,_tmpPresetId,_tmpAddedDurationMinutes,_tmpAddedPrice,_tmpExtendedAt);
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
  public Object getByRentalId(final long rentalId,
      final Continuation<? super List<RentalExtensionEntity>> $completion) {
    final String _sql = "SELECT * FROM rental_extensions WHERE rentalId = ? ORDER BY extendedAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, rentalId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RentalExtensionEntity>>() {
      @Override
      @NonNull
      public List<RentalExtensionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRentalId = CursorUtil.getColumnIndexOrThrow(_cursor, "rentalId");
          final int _cursorIndexOfPresetId = CursorUtil.getColumnIndexOrThrow(_cursor, "presetId");
          final int _cursorIndexOfAddedDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "addedDurationMinutes");
          final int _cursorIndexOfAddedPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "addedPrice");
          final int _cursorIndexOfExtendedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "extendedAt");
          final List<RentalExtensionEntity> _result = new ArrayList<RentalExtensionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RentalExtensionEntity _item;
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
            _item = new RentalExtensionEntity(_tmpId,_tmpRentalId,_tmpPresetId,_tmpAddedDurationMinutes,_tmpAddedPrice,_tmpExtendedAt);
            _result.add(_item);
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
}
