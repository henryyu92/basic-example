package cache

type Element struct{
	Key interface{}
	Value interface{}
	Next *Element
}

type FIFOCache struct{
	head, tail *Element
}

func NewFIFOCache() *FIFOCache{
	return nil
}

func (c *FIFOCache) get(key interface{}) (value interface{}){
	return nil
}

func (c *FIFOCache) set(key, value interface{}){

}