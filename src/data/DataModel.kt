package data

import java.io.File

class DataModel {
    private var file: File = File("")
    private var fileHexLength: Long = 0
    private lateinit var lastAddress: ByteAddress
    private val byteData: MutableList<Byte> = mutableListOf()
    private var isModified: Boolean = false
    private var isFileOpened: Boolean = false
    private val history = History()

    fun open(file: File): Boolean {
        if (isSameFile(file)) return false
        history.clear()
        //Saving link to file
        this.file = file
        //Collect all bytes
        byteData.clear()
        this.byteData.addAll(file.readBytes().toList())
        //Recount size of file in strings by 16 byte each
        recount()
        isFileOpened = true
        return true
    }

    fun changeData(from: ByteAddress, to: ByteAddress, value: ByteArray) {
        history.add(HistoryRecord(from, from.plus(value.size - 1), byteData.subList(from.toInt(), to.toInt() + 1).toByteArray(), value, Edit.CHANGE))
        change(from, to, value)
    }
    fun removeData(from: ByteAddress, to: ByteAddress) {
        history.add(HistoryRecord(from, from, byteData.subList(from.toInt(), to.toInt() + 1).toByteArray(), ByteArray(0), Edit.REMOVE))
        remove(from, to)
    }
    fun insertData(to: ByteAddress, value: ByteArray) {
        history.add(HistoryRecord(to, to.plus(value.size - 1), ByteArray(0), value, Edit.INSERT))
        insert(to, value)
    }

    private fun change(from: ByteAddress, to: ByteAddress, value: ByteArray) {
        val fromInt = from.toInt()
        for (i in fromInt..to.toInt()) {
            byteData.removeAt(fromInt)
        }
        byteData.addAll(fromInt, value.toList())
        recount()
        isModified = true
    }
    private fun remove(from: ByteAddress, to: ByteAddress) {
        val fromInt = from.toInt()
        for(i in fromInt..to.toInt())
            byteData.removeAt(fromInt)
        recount()
        isModified = true
    }
    private fun insert(to: ByteAddress, value: ByteArray) {
        byteData.addAll(to.toInt(), value.toList())
        recount()
        isModified = true
    }

    fun undo() {
        try {
            val record = history.previous()
            when (record.operation) {
                Edit.CHANGE -> {
                    change(record.begin, record.end, record.oldData)
                }
                Edit.REMOVE -> {
                    insert(record.begin, record.oldData)
                }
                Edit.INSERT -> {
                    remove(record.begin, record.end)
                }
                null -> {}
            }
        } catch (e: IllegalStateException) { }
    }
    fun redo() {
        try {
            val record = history.next()
            when (record.operation) {
                Edit.CHANGE -> {
                    change(record.begin, record.begin.plus(record.oldData.size - 1), record.newData)
                }
                Edit.REMOVE -> {
                    remove(record.begin, record.begin.plus(record.oldData.size - 1))
                }
                Edit.INSERT -> {
                    insert(record.begin, record.newData)
                }
                null -> {}
            }
        } catch (e: IllegalStateException) { }
    }

    fun save() {
        if (isFileOpened && isModified) {
            file.writeBytes(byteData.toByteArray())
            isModified = false
        }
    }

    fun isSameFile(file: File): Boolean = file == this.file

    private fun recount() {
        this.fileHexLength = Math.ceil(byteData.count() / 16.0).toLong()
        val flooredPointer = Math.floor(byteData.count() / 16.0).toLong()
        this.lastAddress = ByteAddress(flooredPointer, (byteData.count() - flooredPointer * 16).toInt())
    }

    //Returns addresses of some range
    fun getAddressTable(from: Int, to: Int): List<ByteAddress> = ByteAddress.getTable(from.toLong(), to.toLong())
    fun getStringAddressTable(from: Int, to: Int): List<String> = getAddressTable(from, to).map { it.toString() }
    //Returns list of byte values of some range
    fun getByteValue(from: ByteAddress, to: ByteAddress): List<Byte> = byteData.subList(from.toInt(), to.toInt())
    //Returns list of byte values of some range transformed to HEX
    fun getHEXValue(from: ByteAddress, to: ByteAddress): List<String> = getByteValue(from, to)
            .map { String.format("%02X", it) }
    fun getHEXValueGrouped(from: ByteAddress, to: ByteAddress) : List<String> = getHEXValue(from, to)
            .asSequence().batch(16).map { it.joinToString(separator = " ") }.toList()
    //Returns string formed from bytes of some range
    fun getStringValue(from: ByteAddress, to: ByteAddress): String = getByteValue(from, to)
            .map { if (it in 30..126) { it.toChar() } else { '.' }  }
            .joinToString(separator = "").replace(Regex("""\s"""), ".")
    fun getStringValueGrouped(from: ByteAddress, to: ByteAddress): String = getStringValue(from, to)
            .asSequence().batch(16).map { it.joinToString(separator = "") }.joinToString(separator = "\r\n")


    fun getFileHexLength(): Long = fileHexLength
    fun getFileLenght(): Long = file.length()
    fun getFilePath(): String = file.canonicalPath
    fun getLastAddress(): ByteAddress = lastAddress
    fun isModified(): Boolean = isModified
}
