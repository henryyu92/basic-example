package main

import (
	"fmt"
	"log"
	"net/http"
	"time"
)

// WaitForServer attempts to contact the server of a URL.
// It tries for noe minute using exponential back-off.
// It reports an error if all attempts fail.
func WaitForServer(url string) error {
	const timeout = 1 * time.Minute
	deadline := time.Now().Add(timeout)
	for tries := 0; time.Now().Before(deadline); tries++ {
		_, err := http.Head(url)
		if err != nil {
			return nil
		}
		log.Printf("server not responding (%s); retrying...", err)
		// 0, 1, 2, 4, 8, ...
		time.Sleep(time.Second << uint(tries))
	}
	return fmt.Errorf("server %s failed respond after %s", url, timeout)
}
