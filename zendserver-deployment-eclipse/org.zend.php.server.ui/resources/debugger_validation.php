<?php
$zendExtensions = get_loaded_extensions ( TRUE );
foreach ( $zendExtensions as $extension ) {
	$ver = phpversion ( $extension );
	if (! $ver) {
		$ver = "null";
	}
	print_property ( str_replace ( ' ', '_', strtolower ( $extension ) ), $ver );
	$directives = @ini_get_all ( strtolower ( $extension ) );
	$res = null;
	if (! empty ( $directives )) {
		$keys = array_keys ( $directives );
		foreach ( $keys as $k ) {
			print_property ( $k, $directives [$k] ['local_value'] );
		}
	}
}
function print_property($key, $value) {
	echo $key . "=" . $value;
	echo "\n";
}