function Sequential(Object o) {
	for(var key in o) {
		key(o)
	}
}