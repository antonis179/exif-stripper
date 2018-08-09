package org.amoustakos.utils.analytics;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;
import static org.amoustakos.utils.analytics.EventAction.FAILED;
import static org.amoustakos.utils.analytics.EventAction.LINK_SELECTED;
import static org.amoustakos.utils.analytics.EventAction.LOADED;
import static org.amoustakos.utils.analytics.EventAction.NET_CALL;
import static org.amoustakos.utils.analytics.EventAction.NONE;
import static org.amoustakos.utils.analytics.EventAction.PRESS;
import static org.amoustakos.utils.analytics.EventAction.VIEWED;


@Retention(SOURCE)
@StringDef({
		VIEWED,

		PRESS, LINK_SELECTED,

		NET_CALL,

		LOADED, FAILED,

		NONE
})
public @interface EventAction {
	String

	//Screen
	VIEWED = "Viewed",

	//Button
	PRESS = "Pressed",
	LINK_SELECTED = "Link Selection",

	//Network
	NET_CALL = "Call",

	//Util
	LOADED = "Loaded",
	FAILED = "Failed",

	NONE = "";
}
