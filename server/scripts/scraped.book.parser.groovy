import java.util.regex.Pattern

Set<Book> books = new HashSet<>()

def pattern = Pattern.compile('(\\"[^"]+\\"|[^,]+)\\,(.+)\\,([^",]+|\"\")')

new File('/home/noam/Desktop/books.csv').eachLine {
    def matcher = pattern.matcher(it)
    if (!matcher.matches()) {
        println("Found a weird book : ${it}")
        return
    }
    if (!matcher.group(2)) {
        return
    }
    def book = new Book()
    book.name = matcher.group(1).trim()
    while (book.name.startsWith('"')) {
        book.name = book.name.substring(1)
    }
    while (book.name.endsWith('"')) {
        book.name = book.name.substring(0, book.name.length() - 1)
    }
    book.authors = matcher.group(2).split('\\,').collect {
        def author = it.trim()
        while (author.startsWith('"')) {
            author = author.substring(1)
        }
        while (author.endsWith('"')) {
            author = author.substring(0, author.length() - 1)
        }
        author
    }
    if (matcher.group(3) && !matcher.group(3).equals('""')) {
        book.imageUrl = matcher.group(3)
        while (book.imageUrl.startsWith('"')) {
            book.imageUrl = book.imageUrl.substring(1)
        }
        while (book.imageUrl.endsWith('"')) {
            book.imageUrl = book.imageUrl.substring(0, book.imageUrl.length() - 1)
        }
    }
    if (book.name && book.authors) {
        books << book
    }
}

println "Found ${books.size()} valid books"
println "First book ${books.first()}"
println "Last book ${books.last()}"
println "Multi authored book ${books.find { it.authors.size() > 1 }}"
println "No image URL book ${books.find { !it.imageUrl }}"
println "In name ${books.find { it.name.startsWith('"') || it.name.endsWith('"') }}"
println "In author ${books.find { it.authors.any { it.startsWith('"') || it.endsWith('"') } }}"
println "In URL ${books.find { it.imageUrl && (it.imageUrl.startsWith('"') || it.imageUrl.endsWith('"')) }}"

class Book {
    String name
    Set<String> authors
    String imageUrl


    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Book book = (Book) o

        if (authors != book.authors) return false
        if (imageUrl != book.imageUrl) return false
        if (name != book.name) return false

        return true
    }

    int hashCode() {
        int result
        result = (name != null ? name.hashCode() : 0)
        result = 31 * result + (authors != null ? authors.hashCode() : 0)
        result = 31 * result + (imageUrl != null ? imageUrl.hashCode() : 0)
        return result
    }

    @Override
    public String toString() {
        return """\
Book{
    name='$name',
    authors=$authors,
    imageUrl='$imageUrl'
}"""
    }
}