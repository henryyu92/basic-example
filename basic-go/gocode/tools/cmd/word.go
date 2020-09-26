package cmd

import (
	"github.com/spf13/cobra"
)

var wordCmd = &cobra.Command{
	Use:   "word",
	Short: "单词格式转换",
	Long:  "支持多种单词格式转换",
	Run:   func(cmd *cobra.Command, args []string) {},
}

const (
	MODE_UPPER                         = iota + 1 // 转大写
	MODE_LOWER                                    // 转小写
	MODE_UNDERSCORE_TO_UPPER_CAMELCASE            // 下划线转大写驼峰
	MODE_UNDERSCORE_TO_LOWER_CAMELCASE            // 下划线转小写驼峰
	MODE_CAMELCASE_TO_UNDERSCORE                  // 驼峰转下划线
)
