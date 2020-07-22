package leetcode

// Description:
//	Given a 32-bit signed integer, reverse digits of an integer.
// Example:
//	Input: 123
//	Output: 321
//	Input: -123
//	Output: -321
//	Input: 120
//	Output: 21
// Note:
//	Assume we are dealing with an evironment which could only store integers within 32-bit signed integer range: [-2^31, 2^31-1].
//	For the purpose of this problem, assume that your function returns 0 when the reversed integer overflows.

const int32Max = int(^uint(0) >> 1)
const int32Min = -int32Max - 1

// Approach1: Pop and Push Digits & Check before Overflow
//	We want to repeatedly "pop" the last digit off of x and "push" it to the back of the rev.
//	In the end, rev will be the reverse of the x.
//	However, this approach is dangerous, because the statement temp = rev * 10 + pop can cause overflow.
//	considering [-2^31,2^31-1] = [-2147483648, 2147483647]:
//		if rev > INTMAX/10, then temp = rev * 10 + pop is guaranteed to overflow;
//		if rev == INTMAX/10, than temp = rev * 10 + pop will overflow if and only if pop > 7;
// Time complexity: O(log(x)); There are roughly log10(x) digits in x.
// Space complexity: O(1).
func reverse(x int) int {

	var rev = 0
	for x != 0 {
		pop := x % 10
		x /= 10
		if rev > int32Max/10 || (rev == int32Max/10 && pop > 7) {
			return 0
		}
		if rev < int32Min || (rev == int32Min && pop < -8) {
			return 0
		}
		rev = rev*10 + pop
	}

	return rev
}

// Approach2:
//	when overflow happends, temp = rev * 10 + pop is overflow, so (temp - pop)/10 != rev
func reverse2(x int) int {
	var rev = 0
	for x != 0 {
		pop := x % 10
		temp := rev*10 + pop
		// overflow if not equal
		if (temp-pop)/10 != rev {
			return 0
		}
		rev = temp
		x /= 10
	}

	return rev
}
