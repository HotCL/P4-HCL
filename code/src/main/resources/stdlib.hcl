func else = (bool condition, func[none] action): bool {
    condition not then :action
}

func firstIndexWhere = (list[T] lst, func[T, bool] predicate): num {
    var ret = -1
    var idx = 0
    lst each {
        value predicate and (ret equals -1) then { ret = idx }
        idx = idx + 1
    }
    return ret
}
