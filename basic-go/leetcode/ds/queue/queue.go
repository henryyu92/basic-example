package queue

type (
	Queue struct {
	}
	node struct {
	}
)

func NewQueue() *Queue {
	q := &Queue{}
	return q
}

func (q *Queue) Offer(item interface{}) {

}

func (q *Queue) poll() interface{} {
	return nil
}
