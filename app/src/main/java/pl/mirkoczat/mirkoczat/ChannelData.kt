package pl.mirkoczat.mirkoczat

data class ChannelData(val name : String, val count : Int, val count_all : Int) {
    override fun toString() : String {
        return "#${name} (${count}/${count_all})"
    }
}