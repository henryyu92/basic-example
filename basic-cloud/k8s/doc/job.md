#### Job

Job 用于调配 Pod 对象运行一次性任务，容器中的进程在正常运行完成后不会重启，而是将 Pod 对象设置为 "Completed" 状态。如果容器中的进程因错误而终止，则需要根据配置确定其是否需要重启，未运行完成的 Pod 对象因其所在的节点故障而意外终止后会被重新调度。

Job 对象的 spec 属性中只有 template 是必须的，Job 会自动为创建的 Pod 对象添加 "job-name=JOB_NAME" 和 "controller-uid=UID" 标签，并使用标签选择器完成对 controller-uid 标签的关联：

```yaml
apiVersion: batch/v1
kind: Job
metadata:
  name: job-example
spec:
  template:
    spec:
      restartPolicy: Never
      containers:
      - name: myjob
        image: alpine
        command: ["bin/bash", "-c", "sleep 120"]
```

属性 ```spec.parallelism``` 设置运行任务的并行度，```spec.completions``` 属性设置任务运行的次数，可以以并行的形式运行任务：

```yaml
apiVersion: batch/v1
kind: Job
metadata:
  name: job-multi
spec:
  parallelism: 2
  completions: 5
  template:
    spec:
      restartPolicy: OnFailure
      containers:
      - name: myjob
        image: alpine
        command: ["/bin/sh", "-c", "sleep 20"]
```

```spec.parallelism``` 定义了任务的并行度，即同时运行任务的 Pod 的数量，此属性值支持运行时调整意味着可以实现动态扩容和缩容：

```shell
kubctl scale job <job_name> --replicas=<rep_num>
```

Job 调度的任务在完成后会被清理，如果任务一直运行失败且 ```spec.restartPolicy``` 设置为 OnFailure 则可能导致 Pod 一直处于不停的重启状态。Job 提供了 ```spec.activeDeadlineSeconds``` 属性用于指定任务最大的活动时间长度，```spec.backoffLimit``` 属性用于指定任务失败重试的次数，默认设置为 6

```yaml
spec:
  activeDeadlineSeconde: 100
  backoffLimit: 5
```

#### CronJob

Job 定义的任务在其资源创建之后便会立即执行，但 CronJob 可以周期性的运行任务。CronJob 的 spec 字段可以嵌套使用以下字段：

- ```jobTemplate```：Job 控制器模板，用于为 CronJob 控制器生成 Job 对象，必须
- ```schedule```：Cron 格式的任务调度运行时间点
- ```concurrencyPolicy```：并发执行策略，可用值有 "Allow"，"Forbid" 和 "Replace"，用于定义前一次作业运行尚未完成时是否以及如何运行后一次的作业
- ```failedJobHistoryLimit```：为失败的任务执行保留的历史记录，默认为 1
- ```successfulJobsHistoryLimit```：为成功的任务执行保留的历史记录数，默认为 3
- ```startingDeadlineSeconds```：
- ```suspend```：是否挂起后续的任务执行，默认为 false

```yaml
apiVersion: batch/v1beta1
kind: CronJob
metadata:
  name: cronjob-example
  labels:
    app: mycrojob
spec:
  schedule: "*/2 * * * *"
  jobTemplate:
    metadata:
      labels:
        app: mycronjob-jobs
    spec:
      parallelism: 2
      template:
        spec:
          restartPolicy: OnFailure
          containers:
          - name: myjob
            image: alpine
            command:
            - /bin/sh
            - -c
            - date; echo Hello from the kubernetes cluster; sleep 10
```

#### 