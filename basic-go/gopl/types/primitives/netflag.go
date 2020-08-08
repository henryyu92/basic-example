package primitives

import (
	"fmt"
)

//Flags is a const of net flags
type Flags uint

const (
	//FlagUp is up
	FlagUp = 1 << iota
	//FlagBroadcast supports braodcast access capability
	FlagBroadcast
	//FlagLoopback is a loopback interfaces
	FlagLoopback
	//FlagPointToPoint belongs to a point-to-point link
	FlagPointToPoint
	//FlagMulticast supports multicast access capability
	FlagMulticast
)

//IsUp indicatea whether the net is up
func IsUp(v Flags) bool {
	return v&FlagUp == FlagUp
}

func turnDown(v *Flags) {
	*v &^= FlagUp
}

func setBraodcast(v *Flags) {
	*v |= FlagBroadcast
}

func isCast(v Flags) bool {
	return v&(FlagMulticast|FlagBroadcast) != 0
}

func netflag() {
	var v Flags = FlagMulticast | FlagUp
	// "10001 true"
	fmt.Printf("%b %t\n", v, IsUp(v))
	turnDown(&v)
	// "10000 false"
	fmt.Printf("%b %t\n", v, IsUp(v))
	setBraodcast(&v)
	// "10010 false"
	fmt.Printf("%b %t\n", v, IsUp(v))
	// "10010 true"
	fmt.Printf("%b %t\n", v, isCast(v))
}
