package com.badshun.autoscript;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;

public class MainActivity extends Activity {
    //权限申请号，用于检测权限是否申请成功
    private final int PERMISSION_REQUESTS_CODE = 1;

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //先检查是否拥有权限
        if (!checkPermission()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("没有权限")
                    .setMessage("您还没有给予该软件正常运行所需要的权限")
                    .setPositiveButton("授权", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            applyPermission();
                        }
                    })
                    .setNegativeButton("退出软件", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            System.exit(0);
                        }
                    });
            builder.show();
        } else {
            Toast.makeText(this, "已有权限", Toast.LENGTH_SHORT).show();
        }

        DBHelper dbHelper = new DBHelper(this, "script.db", null, 1);
        db = dbHelper.getWritableDatabase();

        ListView listView = findViewById(R.id.listview);
        listView.setAdapter(new Adapter(dbHelper));
        
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TextView taskNameTV = view.findViewById(R.id.task_name_tv);
                TextView scriptPathTV = view.findViewById(R.id.script_path_tv);

                String taskName = taskNameTV.getText().toString();
                String scriptName = scriptPathTV.getText().toString();

                dbHelper.delete(taskName, scriptName, db);

                listView.setAdapter(new Adapter(dbHelper));

                return true;
            }
        });
        
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "click", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView addImageView = findViewById(R.id.add_iv);
        addImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = View.inflate(MainActivity.this, R.layout.add_alert, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("添加任务")
                        .setView(view)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText taskNameEditText = view.findViewById(R.id.task_name_et);
                                EditText scriptPathEditText = view.findViewById(R.id.script_path_et);

                                //true   Toast.makeText(MainActivity.this, (taskNameEditText.getText().toString().equals("")) + "", Toast.LENGTH_SHORT).show();

                                String taskName = taskNameEditText.getText().toString();
                                String scriptPath = scriptPathEditText.getText().toString();

                                if (!taskName.equals("") && !scriptPath.equals("")) {
//                                    Toast.makeText(MainActivity.this, "非空", Toast.LENGTH_SHORT).show();
                                    dbHelper.insert(taskName, scriptPath, db);
                                    listView.setAdapter(new Adapter(dbHelper));
                                } else {
                                    Toast.makeText(MainActivity.this, "内容不能为空", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (!checkPermission()) {
            finish();
            System.exit(0);
        } else {
            Toast.makeText(this, "已成功授权", Toast.LENGTH_SHORT).show();
        }
    }

    //检测是否已经获取权限
    private boolean checkPermission() {
        if (!Settings.canDrawOverlays(this)) {
            return false;
        }

        return true;
    }

    //申请权限
    private void applyPermission() {
        startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName())),
                        PERMISSION_REQUESTS_CODE);

    }

    private class Adapter extends BaseAdapter {
        DBHelper dbHelper;
        ArrayList<Script> scriptList;

        public Adapter(DBHelper dbHelper) {
            this.dbHelper = dbHelper;

            scriptList = dbHelper.query(db);
        }

        @Override
        public int getCount() {
            return scriptList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                view = View.inflate(MainActivity.this, R.layout.script_item, null);
            } else {
                view = convertView;
            }

            TextView taskNameTV = view.findViewById(R.id.task_name_tv);
            taskNameTV.setText(scriptList.get(position).getTaskName());

            TextView scriptPathTV = view.findViewById(R.id.script_path_tv);
            scriptPathTV.setText(scriptList.get(position).getScriptPath());

            return view;
        }
    }
}