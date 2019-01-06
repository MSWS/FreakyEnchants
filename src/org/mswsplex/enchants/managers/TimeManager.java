package org.mswsplex.def.managers;

public class TimeManager {
	public static String getTime(Double mils) {
		boolean isNegative = mils < 0;
		double mil = Math.abs(mils);
		String names[] = { "milliseconds", "seconds", "minutes", "hours", "days", "weeks", "months", "years", "decades",
				"centuries" };
		String sNames[] = { "millisecond", "second", "minute", "hour", "day", "week", "month", "year", "decade",
				"century" };
		Double length[] = { 1.0, 1000.0, 60000.0, 3.6e+6, 8.64e+7, 6.048e+8, 2.628e+9, 3.154e+10, 3.154e+11,
				3.154e+12 };
		String suff = "";
		for (int i = length.length - 1; i >= 0; i--) {
			if (mil >= length[i]) {
				if (suff.equals(""))
					suff = names[i];
				mil = mil / length[i];
				if (mil == 1) {
					suff = sNames[i];
					// suff = suff.substring(0, suff.length() - 1);
				}
				break;
			}
		}
		String name = mil + "";
		if (Math.round(mil) == mil) {
			name = (int) Math.round(mil) + "";
		}
		if (name.contains(".")) {
			if (name.split("\\.")[1].length() > 2) {
				name = name.split("\\.")[0] + "."
						+ name.split("\\.")[1].substring(0, Math.min(name.split("\\.")[1].length(), 2));
			}
		}
		if (isNegative)
			name = "-" + name;
		return name + " " + suff;
	}

	public static double getMills(String msg) {
		String val = "";
		double mills = -1;
		for (char c : msg.toCharArray()) {
			if ((c + "").matches("[0-9\\.-]")) {
				val = val + c;
			} else {
				break;
			}
		}
		try {
			mills = Double.valueOf(val) * 1000;
		} catch (Exception e) {
			return 0.0;
		}

		Double amo[] = { 60.0, 3600.0, 86400.0, 604800.0, 2.628e+6, 3.154e+7, 3.154e+8, 3.154e+9 };
		String[] names = { "m", "h", "d", "w", "mo", "y", "de", "c" };
		for (int i = amo.length - 1; i >= 0; i--) {
			if (msg.toLowerCase().contains(names[i])) {
				mills = mills * amo[i];
				break;
			}
		}
		return mills;
	}
}
