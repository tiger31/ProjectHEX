package data

import java.io.File

class DataModel {
    private lateinit var file: File
    private var fileHexLength: Long = 0
    private val byteAddress: MutableList<ByteAddress> = mutableListOf()
    private val byteData: MutableList<Byte> = mutableListOf()

    fun open(filename: String) {
        val file = File(filename)
        if (file.exists() && file.canRead() && file.canWrite()) {
            //Saving link to file
            this.file = file
            //Collect all bytes
            byteData.addAll(file.readBytes().toList())
            //Recount size of file in strings by 16 byte each
            fileHexLength = Math.ceil(byteData.count() / 16.0).toLong()
            //Forming the addresses table
            this.createAddressTable()
        }
    }


    private fun createAddressTable() = (0..(fileHexLength))
            .forEachIndexed { i, l -> byteAddress.add(ByteAddress(i.toLong())) }

    //Returns whole address table
    public fun getAddressTable(): List<ByteAddress> = byteAddress
    //Returns addresses of some range
    public fun getAddressTable(from: Int, to: Int): List<ByteAddress> = byteAddress.subList(from, to)
    public fun getStringAddressTable(from: Int, to: Int): List<String> = getAddressTable(from, to).map { it.toString() }
    //Returns list of byte values of some range
    public fun getByteValue(from: ByteAddress, to: ByteAddress): List<Byte> = byteData.subList(from.toInt(), to.toInt())
    //Returns list of byte values of some range transformed to HEX
    public fun getHEXValue(from: ByteAddress, to: ByteAddress): List<String> = getByteValue(from, to)
            .map { String.format("%02X", it) }
    public fun getHEXValueGrouped(from: ByteAddress, to: ByteAddress) : List<String> = getHEXValue(from, to)
            .asSequence().batch(16).map { it.joinToString(separator = " ") }.toList()
    //Returns string formed from bytes of some range
    public fun getStringValue(from: ByteAddress, to: ByteAddress): String = String(getByteValue(from, to).toByteArray())
            .replace(Regex("""\s"""), ".")
    public fun getStringValueGrouped(from: ByteAddress, to: ByteAddress): String = getStringValue(from, to)
            .asSequence().batch(16).map { it.joinToString(separator = "") }.joinToString(separator = "\r\n")


    public fun getFileHexLength(): Long = fileHexLength
    public fun getFileLenght(): Long = file.length()
}
