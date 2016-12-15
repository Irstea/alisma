/**
 * 
 */
package utils;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.text.DefaultFormatterFactory;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.calendar.DatePickerFormatter;
import org.jdesktop.swingx.plaf.basic.BasicDatePickerUI;
import org.joda.time.DateTime;

/**
 * @author quinton
 * 
 */
@SuppressWarnings("serial")
public class DatePicker extends JXDatePicker {
	Object obj = this;
	String[] formats = { "dd/MM/yyyy", "dd/MM/yy" };
	SimpleDateFormat longFormat = new SimpleDateFormat(formats[0]);
	SimpleDateFormat shortFormat = new SimpleDateFormat(formats[1]);
	boolean enabled = true;
	MouseListener mouseListener = new MouseListener() {
		public void mouseClicked(MouseEvent me) {
			BasicDatePickerUI date_ui = ((BasicDatePickerUI) ((JXDatePicker) obj)
					.getUI());
			date_ui.toggleShowPopup();
		}

		public void mouseEntered(MouseEvent me) {
		}

		public void mouseExited(MouseEvent me) {
		}

		public void mouseReleased(MouseEvent me) {
		}

		public void mousePressed(MouseEvent me) {
		}
	};


	public DatePicker() {
		super();
		init();

	}

	public DatePicker(Date selected) {
		super(selected);
		init();
	}

	DatePicker(Date selection, Locale locale) {
		super(selection, locale);
		init();
	}



	/**
	 * Affiche le paneau de selection de la date au clic de souris
	 */
	private void init() {
		obj = this;
		getEditor().addMouseListener(mouseListener);
		DatePickerFormatter formatter = new DatePickerFormatter(
		// invers sequence for parsing to satisfy the year parsing rules
				new DateFormat[] { shortFormat, longFormat }) {

			@Override
			public String valueToString(Object value) throws ParseException {
				if (value == null)
					return null;
				return getFormats()[1].format(value);
			}
		};
		DefaultFormatterFactory factory = new DefaultFormatterFactory(formatter);
		getEditor().setFormatterFactory(factory);
		setFormats(formats);
		getEditor().setDisabledTextColor(Color.BLACK);
	}

	/**
	 * Active ou desactive le controle
	 */
	public void setEnabled(boolean enabled) {
		if (this.enabled != enabled) {
			if (enabled) {
				getEditor().addMouseListener(mouseListener);
			} else 
				getEditor().removeMouseListener(mouseListener);
			this.enabled = enabled;
			//getEditor().setEnabled(enabled);
			super.setEnabled(enabled);
		}
	}

	/**
	 * Retourne la date au format SQL (yyyy-MM-dd)
	 * 
	 * @return
	 */
	public String getDateSql() {
		DateTime dt = new DateTime(this.getDate());
		String fmt = "%02d";
		return dt.getYear() + "-" + String.format(fmt, dt.getMonthOfYear())
				+ "-" + String.format(fmt, dt.getDayOfMonth());
	}
}
