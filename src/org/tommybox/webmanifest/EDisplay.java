package org.tommybox.webmanifest;

public enum EDisplay {
	/**
	 * https://w3c.github.io/manifest/#display-modes
	 * https://w3c.github.io/manifest/#dfn-display-modes-values
	 */

	//@formatter:off

	/* standard */
	FULLSCREEN      ,
	STANDALONE      ,
	MINIMAL_UI      ,
	BROWSER         , // default

	/* non standard */
	FRAMELESS_WINDOW,
	MINIMIZED_WINDOW,
	MAXIMIZED_WINDOW,
	DESKTOP_AREA    ,
	HEADLESS        ;

	//@formatter:on

}
