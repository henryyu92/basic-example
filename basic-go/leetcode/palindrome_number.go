package leetcode

// Description:
//	Determine whether an integer is a palindrome.
//	An integer is a palindrome when it reads the same backward as forward.
// Example:
//	Input: 121
//	Output: true
//	Input: -121
//	Output: false
//	Input: 10
//	Output: false

// Approach 1: Revert half of the number
// Intution:
//	The first idea that comes to mind is to convert the number into string, and check if the string is a palindrome,
//	but this would require extra non-constant space for creating the string which is not allowed by the problem
//	description.
//	Second idea would be reverting the number itself, and then compare the number with original number, if they are
//	the same, then the number is a palindrome. However, if the reversed number is larger than Int.Max, we will hit
//	integer overflow problem.
//	Following the thoughts based on the second idea, to avoid the overflow issue of the reverted number, what if we
//	only revert half of the int number? After all, the reverse of the last half of the palindrome should be the same as
//	the first half of the number, if the number is a palindrome.
//	For example, if the input is 1221, if we can revert the lasf part of the number of "1221" from "21" to "12", and
//	compare it with the first half of the number "12", since 12 is the same with 12, we know that the number is a palindrome.
// Algorithm:
//	First of all we should take care of some edge cases. All negative numbers are not palindrome, So we can return
//	false for all negative numbers.
//	For number 1221, if we do 1221%10, we get the last digit 1, to get the second to the last digit, we need to remove
//	the last digit from 1221, we could do so by dividing it by 10, 1221/10=122. Then we can get the last digit again
//	by doing a modulus by 10, 122%10=2, and if we multiply the last digit by 10 and add the second last digit, 1*10+2=12,
//	it gives us the reverted number we want.Continuing this process would give us the reverted number more digits.
//	How do we konw that we've reached the last of the number?
//	Since we divided the number by 10, and multiplied the reversed number by 10, when the original number is less than
//	the reversed number, it means we've processed half of the number digits.
// Time complexity: O(log10(n))
// Space complexity: O(1)
func isPalindrome(x int) bool {
	if x < 0 || (x%10 == 0 && x != 0) {
		return false
	}

	var rev int
	for x > rev {
		rev = rev*10 + x%10
		x /= 10
	}

	return x == rev || x == rev/10
}
