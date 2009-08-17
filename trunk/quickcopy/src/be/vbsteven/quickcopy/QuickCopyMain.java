package be.vbsteven.quickcopy;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class QuickCopyMain extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle("Quickcopy");
		
		refresh();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		refresh();
	}

	// setup options menu
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuAdd:
			addEntry();
			break;
		case R.id.menuHelp:
			showHelp();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showHelp() {
		TextView tv = new TextView(this);
		tv
				.setText("1. Use the menu button to add new entries to the list"
						+ "\n\n2. Tap an existing entry to copy that entry onto the clipboard"
						+ "\n\n3. Long press on an existing entry to edit or delete that entry");

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Instructions").setView(tv).setCancelable(true)
				.setNeutralButton("OK", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void addEntry() {

		final EditText edit = new EditText(this);
		edit.setEms(20);
		edit.setLines(5);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Add a new entry").setView(edit).setCancelable(true)
				.setPositiveButton("Add",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								String value = edit.getText().toString();
								if (!value.equals("")) {
									DBHelper.get(QuickCopyMain.this).addEntry(
											value);
									refresh();
								}
							}
						}).setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		AlertDialog alert = builder.create();
		alert.show();
	}

	protected void refresh() {
		setContentView(R.layout.main);
		DBHelper db = DBHelper.get(this);
		
		ArrayList<String> entries = db.getEntries();
		if (entries.size() == 0) {
			TextView tv = (TextView)findViewById(R.id.textEntries);
			tv.setText("Your list of entries is still empty.\n\nUse the menu button on your device to add new entries to your Quickcopy list");
		} else {
			TextView tv = (TextView)findViewById(R.id.textEntries);
			tv.setText("Tap to copy / Long press to edit");

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					R.layout.simple_list_item_1, entries);
			ListView lv = (ListView) findViewById(R.id.valuesList);
			lv.setAdapter(adapter);

			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					String value = (String) arg0.getAdapter().getItem(arg2);
					ClipboardManager manager = (ClipboardManager) QuickCopyMain.this
							.getSystemService(CLIPBOARD_SERVICE);
					manager.setText(value);
					Toast t = Toast.makeText(QuickCopyMain.this, "copied \"" + value
							+ "\"" + " to the clipboard", 6000);
					t.show();
					finish();
				}

			});

			lv.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					Intent i = new Intent();
					i.setClass(QuickCopyMain.this, EditEntryActivity.class);
					i.putExtra("entry", (String) arg0.getAdapter()
							.getItem(arg2));
					startActivityForResult(i, 1);
					return false;
				}

			});
		}
	}

}