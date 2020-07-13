package ch1

import (
	"fmt"
	"io"
	"io/ioutil"
	"net/http"
	"os"
	"time"
)

func fetchall() {
	start := time.Now()
	ch := make(chan string)

	for _, url := range os.Args[1:] {
		// start a goroutine
		go fetch1(url, ch)
	}
	for range os.Args[1:] {
		// receive from channel ch
		fmt.Println(<-ch)
	}
	fmt.Printf("%.3fs elapsed\n", time.Since(start).Seconds())
}

func fetch1(url string, ch chan<- string) {
	start := time.Now()
	resp, err := http.Get(url)
	if err != nil {
		ch <- fmt.Sprint(err)
		return
	}
	nbytes, err := io.Copy(ioutil.Discard, resp.Body)
	resp.Body.Close()
	if err != nil {
		ch <- fmt.Sprint(err)
		return
	}

	secs := time.Since(start).Seconds()
	ch <- fmt.Sprintf("%.2fs  %7d  %s", secs, nbytes, url)
}
