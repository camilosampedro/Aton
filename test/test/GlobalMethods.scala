package test

/**
  * Created by camilo on 9/11/16.
  */
object GlobalMethods {
  def isSorted[T](list: List[T])(compare: (T, T) => Boolean): Boolean = {
    list.sliding(2).map({ case List(a, b) => compare(a,b) }).forall(identity)
  }
}
