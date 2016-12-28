package com.conta.comer.data.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.conta.comer.data.entity.BaseInfo;
import com.conta.comer.data.entity.City;
import com.conta.comer.data.entity.Customer;
import com.conta.comer.data.entity.CustomerPic;
import com.conta.comer.data.entity.Goods;
import com.conta.comer.data.entity.GoodsGroup;
import com.conta.comer.data.entity.KeyValue;
import com.conta.comer.data.entity.Payment;
import com.conta.comer.data.entity.Position;
import com.conta.comer.data.entity.Province;
import com.conta.comer.data.entity.QAnswer;
import com.conta.comer.data.entity.Question;
import com.conta.comer.data.entity.Questionnaire;
import com.conta.comer.data.entity.SaleOrder;
import com.conta.comer.data.entity.SaleOrderItem;
import com.conta.comer.data.entity.VisitInformation;
import com.conta.comer.data.entity.VisitLine;

/**
 * Created by Mahyar on 6/4/2015.
 */
public class CommerDatabaseHelper extends SQLiteOpenHelper
{

    public static final String TAG = CommerDatabaseHelper.class.getSimpleName();
    public static final String DATABASE_NAME = "Commer";
    public static final Integer DATABASE_VERSION = 2;

    private static CommerDatabaseHelper sInstance;

    public CommerDatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    public static synchronized CommerDatabaseHelper getInstance(Context context)
    {

        if (sInstance == null)
        {
            sInstance = new CommerDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Log.i(TAG, "start creating tables");
        db.execSQL(KeyValue.CREATE_TABLE_SQL);
        db.execSQL(Province.CREATE_TABLE_SQL);
        db.execSQL(City.CREATE_TABLE_SQL);
        db.execSQL(Customer.CREATE_TABLE_SQL);
        db.execSQL(BaseInfo.CREATE_TABLE_SQL);
        db.execSQL(VisitLine.CREATE_TABLE_SQL);
        db.execSQL(VisitInformation.CREATE_TABLE_SQL);
        db.execSQL(Questionnaire.CREATE_TABLE_SQL);
        db.execSQL(Question.CREATE_TABLE_SQL);
        db.execSQL(QAnswer.CREATE_TABLE_SQL);
        db.execSQL(GoodsGroup.CREATE_TABLE_SQL);
        db.execSQL(Goods.CREATE_TABLE_SQL);
        db.execSQL(SaleOrder.CREATE_TABLE_SCRIPT);
        db.execSQL(SaleOrderItem.CREATE_TABLE_SCRIPT);
        db.execSQL(CustomerPic.CREATE_TABLE_SQL);
        db.execSQL(Payment.CREATE_TABLE_SCRIPT);
        db.execSQL(Position.CREATE_TABLE_SCRIPT);
        Log.i(TAG, "end of creating tables");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.i(TAG, "on Database Upgrade");
//        db.execSQL(Position.CREATE_TABLE_SCRIPT);
    }
}
