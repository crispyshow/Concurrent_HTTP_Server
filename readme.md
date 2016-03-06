# Concurrent-HTTP-Server

This project implements an HTTP Server, which
1) serves static files and directories from local file system
2) support HTTP/1.0 protocol, multiple MIME types including HTML, TXT, JavaScript, CSS, PNG, and JPG files
3) only support GET requests
4) safely support multiple real (e.g. from Chrome, Safari browsers) concurrent client requests
5) bases only on client/server socket operations (e.g. ServerSocket in Java)
6) is tested with auto-testing scripts and/or tools along with real browser tests
7) is benchmarked by ab
6) is written in Java