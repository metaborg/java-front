/* Mutually recursive generic bounds (pg 212, Example 8.1.2-1) */
interface ConvertibleTo<T> {}
class ReprChange<T extends ConvertibleTo<S>, S extends ConvertibleTo<T>> {}
