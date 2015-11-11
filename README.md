Interruptions Locale Plugin
===========================

A Locale plugin to modify the Lollipop interruption setting (also known as "zen mode") programatically.
The plugin supports setting the interruption setting to the modes all, priority or none. However, due
to Android API limitations, setting the mode activates the indefinite time constraint.

Build variants
--------------

Building the plugin using the `pluginOnlyRelease` variant should be enough. The `additionalActivity`
flavor include an extra activity useful for debugging purposes.

Usage
-----

After installing the app, any Locale-compatible host application (like Locale itself or
[Llama](https://play.google.com/store/apps/details?id=com.kebab.Llama)) should provide a plugin entry
called "Interruptions Locale Plugin" to configure the plugin. Nevertheless, before this plugin can be
used, you have to grant it notification access. This condition is checked every time you open the
settings of this plugin.

License
-------

See [LICENSE](LICENSE).