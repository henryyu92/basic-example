package ch11

import (
	"fmt"
	"log"
	"net/smtp"
)

func bytesInUse(username string) int64 {
	return 0
}

const sender = "notifications@example.com"
const passowrd = "correcthorsebatterystaple"
const hostname = "smtp.example.com"

const template = `Warning: you are using %d bytes of storage, %d%% of your quota`

// CheckQuota check quota usage
func CheckQuota(username string) {
	used := bytesInUse(username)
	const quota = 1000 * 1000 * 1000
	percent := 100 * used / quota
	if percent < 90 {
		return
	}
	msg := fmt.Sprintf(template, used, percent)
	notifyUser(username, msg)
}

var notifyUser = func(username, msg string) {
	auth := smtp.PlainAuth("", sender, passowrd, hostname)
	err := smtp.SendMail(hostname+":587", auth, sender, []string{username}, []byte(msg))
	if err != nil {
		log.Printf("smtp.SendMail(%s) failed: %s", username, err)
	}
}
