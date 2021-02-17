### 基础命令

- git log：查看提交日志
- git remote -v：查看远程仓库详情
- git remote add <remote_repo_name> <repo_url>：添加远程仓库
- git fetch <remote_repo>：拉取远程仓库的所有分支
- git remote rename <old> <new>：重命名远程仓库
- git remote rm <repo_name>：删除远程仓库
- git clone <repo_url>：克隆仓库到本地

github fork 仓库同步：
- fork 的仓库 clone 到本地 ```git clone <repo_name>```
- 本地仓库增加源远程仓库 ```git remote add <repo_name> <remote_repo_url>```
- 拉取源远程仓库到本地 ```git fetch <repo_name>```
- 合并拉取的源远程仓库分支 ```git merge <repo_name>/<branch_name>```
- 本地仓库推送到 fork 仓库 ```git push origin <branch_name>```
- fork 仓库发起 pull request 到源仓库

```shell
git remote add upstream git@github.com/apache/dubbo.git
git fetch upstream
git rebase upstream/master
git checkout -b bug#1234

git push origin bug#1234
```

```shell
git remote add upstream https://github.com/apache/pulsar
git fetch upstream
# 创建 bug 分支
git checkout -b bug#1234 upstream/master
git pull --rebase

git push origin bug#1234 --force
```
### 分支命令
- ```git branch```：查看本地所有分支
- ```git branch --remote```：查看远程分支
- ```git branch -a```：查看所有本地和远程分支
- ```git branch <branchname>```：新建分支
- ```git merge <branchname>```：合并分支到当前分支
- ```git branch -d <branchname>```：删除分支
- ```git push origin <branchname>```：推送分支到远程对应分支上
- ```git checkout -b <branchname> origin/<branchname>```：创建与远程分支对应的分支
- ```git branch --set-upstream-to=origin/<branchname> <branchname>```：建立本地分支与远程分支的关联
- ```git push origin --delete <branchname>```：删除远程分支
### 撤销修改
- 如果只是修改了工作区的内容，还未使用 git add 将修改添加到暂存区，使用 ```git checkout -- <filename>``` 撤销对文件的修改
- 如果已经使用 git add 将修改添加到暂存区，使用 ```git reset HEAD <filename>``` 返回到 HEAD 的版本，此时暂存区会被清空；然后使用 git reset -- <filename> 清空工作区
- 如果已经使用 git commit 将修改添加到版本库了，使用 ```git reset --hard <commit_id>``` 将 HEAD 回滚到指定的提交

- ```git commit --amend```
### git reset

### git fetch
git fetch 是将远程仓库的最新内容拉到本地，用户在检查了以后决定是否合并到本地仓库分支中。git pull 是将远程仓库最新内容拉下来后直接合并，即 git pull = git fetch + git merge 这样可能会产生冲突，需要手动解决冲突。

- git fetch <远程主机>：将远程主机的更新全部拉取到本地
- git fetch <远程主机> <分支>：将远程主机的指定分支更新拉取到本地
### git cherry-pick
git cherry-pick 通常用于把特定提交从仓库的一个分支引入到其他分支中。
### git rebase
使用 git merge 提交时由于分支有其他提交，因此会将两个分支的最新快照(c3 和 c4)以及二者最近的共同祖先(c2)进行三方合并生成一个新的快照(c5)并提交，因而导致提交历史出现了分叉。
```
c0 <-- c1 <-- c2 |<-- c3(dev) <----|<--c5
                 |<-- c4(master)<--|
```
git rebase 是一种变基，将 dev 分支上的修改都移动到 master 分支上。原理是首先找到这两个分支(当前分支 dev，目标基底分支 master)的最近共同祖先(c2)然后对比当前分支相对于该祖先的历次提交，提取相应的修改存为临时文件，然后将当前分支指向目标基底(c3)，最后将之前的临时文件的修改依次应用：
```
c0 <-- c1 <-- c2 |<-- c4(dev)
                 |<-- c3(master)<--c4'(dev)
```
变基完成之后进行一次合并，即可合并两个分支：
```
c0 <-- c1 <-- c2 <-- c3<--c4'(dev, master)             
```
变基存在一定的风险，使用变基需要遵守一条准则：**不要在仓库外有副本的分支执行变基**

### git stash

`git stash` 命令用于将本地的修改暂存，修改必须是未提交到暂存区。