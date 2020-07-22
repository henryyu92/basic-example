package leetcode

// Description:
// 	Given an array of integers, return indices of the tow numbers such that they add up to a specific target.
// 	You may assume that each input would have exactly one solution, and you may not use the same element twice.
// Example:
//	Given nums = [2, 7, 11, 15], target = 9
//	Because nums[0] + nums[1] = 2 + 7 = 9
//	return [0, 1]

// Approach 1: Brute Force
//	Time completexity: O(n^2); For each element, we try to find its complement by looping through the rest of
//	array which takes O(n) time. Therefore, the time complexity is O(n^2).
//	Space comlexity: O(1)
func bruteForce(nums []int, target int) []int {

	for i, v := range nums {
		for j, w := range nums {
			if v+w == target {
				return []int{i, j}
			}
		}
	}
	return nil
}

// Approach 2: Two-pass Hash Table
//	Time complexity: O(n); A hash table support fast look up in near constant time O(1).
//	Space complexity: O(n); the extral space required depends on the number of items stored in the hash table,
//	which is stores exactly n elements.
func twoPassHash(nums []int, target int) []int {

	m := make(map[int]int)
	// init map
	for i, v := range nums {
		m[v] = i
	}

	for i, v := range nums {
		complement := target - v
		if t, ok := m[complement]; ok {
			return []int{i, t}
		}
	}
	return nil
}

// Approach 3: One-pass Hash Table; While we iterate and inserting elements into table, we also look back to
//	check if current element's complement already exists in the table. If it exists, we have found a solution return
//	immediately
// Time complexity: O(n)
// Space complexity: O(n)
func onePassHash(nums []int, target int) []int {

	m := make(map[int]int)
	for i, v := range nums {
		complement := target - v
		if _, ok := m[complement]; ok {
			return []int{complement, i}
		}
		m[v] = i
	}

	return nil
}
