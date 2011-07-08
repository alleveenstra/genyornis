var count = 1;

function event_handler(from, message) {
	pipe.send("test", "Msg recv " + count + ": " + message);
	count += 1;
}

pipe.join("proxy", application, "event_handler");