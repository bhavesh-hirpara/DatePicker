package android.picker;

import java.util.Calendar;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class TimePicker extends LinearLayout {

	private View myPickerView;

	private Button hour_plus;
	private EditText hour_display;
	private Button hour_minus;

	private Button min_plus;
	private EditText min_display;
	private Button min_minus;

	private Button am_pm;

	private Calendar cal;

	private boolean isAMPMVisible = true;

	public static final int HOUR_12 = 12;
	public static final int HOUR_24 = 24;

	private int currentTimeFormate = HOUR_12;

	public TimePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context mContext) {
		LayoutInflater inflator = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		myPickerView = inflator.inflate(R.layout.timepicker, null);
		this.addView(myPickerView);

		initializeReference();
	}

	private void initializeReference() {

		hour_plus = (Button) myPickerView.findViewById(R.id.hour_plus);
		hour_plus.setOnClickListener(hour_plus_listener);
		hour_display = (EditText) myPickerView.findViewById(R.id.hour_display);
		hour_display.addTextChangedListener(hour_watcher);
		hour_minus = (Button) myPickerView.findViewById(R.id.hour_minus);
		hour_minus.setOnClickListener(hour_minus_listener);

		min_plus = (Button) myPickerView.findViewById(R.id.min_plus);
		min_plus.setOnClickListener(min_plus_listener);
		min_display = (EditText) myPickerView.findViewById(R.id.min_display);
		min_display.addTextChangedListener(min_watcher);
		min_minus = (Button) myPickerView.findViewById(R.id.min_minus);
		min_minus.setOnClickListener(min_minus_listener);

		am_pm = (Button) myPickerView.findViewById(R.id.am_pm);
		am_pm.setOnClickListener(am_pm_listener);

		cal = Calendar.getInstance();

		initData();
		initFilterNumericDigit();

	}

	private void initData() {

		if (currentTimeFormate == HOUR_12) {
			hour_display.setText(String.valueOf(cal.get(Calendar.HOUR)));
			sendToDisplay();
		} else {
			hour_display.setText(String.valueOf(cal.get(Calendar.HOUR_OF_DAY)));
		}

		min_display.setText(String.valueOf(cal.get(Calendar.MINUTE)));
	}

	private void initFilterNumericDigit() {

		try {
			if (currentTimeFormate == HOUR_12) {
				hour_display
						.setFilters(new InputFilter[] { new InputFilterMinMax(
								0, 11) });
			} else {
				hour_display
						.setFilters(new InputFilter[] { new InputFilterMinMax(
								0, 23) });
			}

			min_display.setFilters(new InputFilter[] { new InputFilterMinMax(0,
					59) });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setTimeChangedListener(TimeWatcher listener) {
		this.mTimeWatcher = listener;
	}

	public void removeTimeChangedListener() {
		this.mTimeWatcher = null;
	}

	View.OnClickListener hour_plus_listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			hour_display.requestFocus();

			try {
				if (currentTimeFormate == HOUR_12) {
					cal.add(Calendar.HOUR, 1);
				} else {
					cal.add(Calendar.HOUR_OF_DAY, 1);
				}
				sendToDisplay();
			} catch (Exception e) {
				Log.e("", e.toString());

			}
		}
	};
	View.OnClickListener hour_minus_listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			hour_display.requestFocus();

			try {
				if (currentTimeFormate == HOUR_12) {
					cal.add(Calendar.HOUR, -1);
				} else {
					cal.add(Calendar.HOUR_OF_DAY, -1);
				}
				sendToDisplay();
			} catch (Exception e) {
				Log.e("", e.toString());
			}
		}
	};

	View.OnClickListener min_plus_listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			min_display.requestFocus();

			try {
				cal.add(Calendar.MINUTE, 1);
				sendToDisplay();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	View.OnClickListener min_minus_listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			min_display.requestFocus();

			try {
				cal.add(Calendar.MINUTE, -1);
				sendToDisplay();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	View.OnClickListener am_pm_listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			try {
				if (cal.get(Calendar.AM_PM) == Calendar.AM) {
					cal.set(Calendar.AM_PM, Calendar.PM);
				} else {
					cal.set(Calendar.AM_PM, Calendar.AM);
				}

				sendToDisplay();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	class InputFilterMinMax implements InputFilter {

		private int min, max;

		public InputFilterMinMax(int min, int max) {
			this.min = min;
			this.max = max;
		}

		public InputFilterMinMax(String min, String max) {
			this.min = Integer.parseInt(min);
			this.max = Integer.parseInt(max);
		}

		@Override
		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {
			try {
				int input = Integer.parseInt(dest.toString()
						+ source.toString());
				if (isInRange(min, max, input)) {
					return null;
				}
			} catch (NumberFormatException nfe) {
			}
			return "";
		}

		private boolean isInRange(int a, int b, int c) {
			return b > a ? c >= a && c <= b : c >= b && c <= a;
		}
	}

	public void reset() {
		cal = Calendar.getInstance();
		initFilterNumericDigit();
		initData();
		sendToDisplay();
	}

	synchronized private void sendToListener() {

		if (mTimeWatcher != null) {
			if (currentTimeFormate == HOUR_12) {
				mTimeWatcher.onTimeChanged(cal.get(Calendar.HOUR),
						cal.get(Calendar.MINUTE), cal.get(Calendar.AM_PM));
			} else {
				mTimeWatcher.onTimeChanged(cal.get(Calendar.HOUR_OF_DAY),
						cal.get(Calendar.MINUTE), -1);
			}
		}

	}

	private void sendToDisplay() {

		if (currentTimeFormate == HOUR_12) {
			hour_display.setText(String.valueOf(cal.get(Calendar.HOUR)));
		} else {
			hour_display.setText(String.valueOf(cal.get(Calendar.HOUR_OF_DAY)));
		}
		min_display.setText(String.valueOf(cal.get(Calendar.MINUTE)));

		if (isAMPMVisible) {
			if (cal.get(Calendar.AM_PM) == Calendar.AM) {
				am_pm.setText("AM");
			} else {
				am_pm.setText("PM");
			}
		}

	}

	TimeWatcher mTimeWatcher = null;

	public interface TimeWatcher {
		void onTimeChanged(int h, int m, int am_pm);
	}

	TextWatcher hour_watcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			try {
				if (s.toString().length() > 0) {
					if (currentTimeFormate == HOUR_12) {
						cal.set(Calendar.HOUR, Integer.parseInt(s.toString()));
					} else {
						cal.set(Calendar.HOUR_OF_DAY,
								Integer.parseInt(s.toString()));
					}

					sendToListener();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	TextWatcher min_watcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			try {
				if (s.toString().length() > 0) {
					cal.set(Calendar.MINUTE, Integer.parseInt(s.toString()));
					sendToListener();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	public void setCurrentTimeFormate(int currentTimeFormate) {
		this.currentTimeFormate = currentTimeFormate;
		if (currentTimeFormate == HOUR_24) {
			isAMPMVisible = false;
			am_pm.setVisibility(View.GONE);
		}

		initFilterNumericDigit();
		sendToDisplay();
	}

	public int getCurrentTimeFormate() {
		return currentTimeFormate;
	}

	public void setAMPMVisible(boolean isAMPMVisible) {
		this.isAMPMVisible = isAMPMVisible;
		if (!isAMPMVisible) {
			am_pm.setVisibility(View.GONE);
		}
	}

	public void setCalender(Calendar c) {
		cal = c;

		initFilterNumericDigit();
		sendToDisplay();
	}
}
