package org.firstinspires.ftc.teamcode.Blobs.blobdetect

import org.firstinspires.ftc.teamcode.Blobs.image.Color
import org.firstinspires.ftc.teamcode.Blobs.image.Pixel
import spock.lang.Specification

class BlobDetectionSpec extends Specification {

    def blobDetection = new BlobDetection()

    def random = new Random()

    def m = [
            'R': Color.RED,
            'G': Color.GREEN,
            'B': Color.BLUE,
            'w': Color.WHITE,
            'e': Color.GREY,
            'a': Color.BLACK,
            'Y': Color.YELLOW
    ]

    def "fails on null input"() {
        when:
        blobDetection.getBlobs(null)

        then:
        thrown NullPointerException
    }

    def "nothing on empty input"() {
        when:
        def blobs = blobDetection.getBlobs([[] as Pixel[]] as Pixel[][])

        then:
        blobs.isEmpty()
    }

    def "too big"() {
        when:
        def pixels = pixelate """
                YYYYY
                YYYYY
                YYYYY
                YYYYY
                YYYYY
                """
        def blobs = blobDetection.getBlobs(pixels)

        then:
        blobs.isEmpty()

    }

    def "just right"() {
        when:
        def pixels = pixelate """
                wwwwwwwwwwww
                wwwwwwwwwwww
                wwwwwwwwwwww
                wwwwwwwwwwww
                wwwwwwwwwwww
                wwwwwwwwwwww
                wwwwwwwwwwww
                YYYYYwwwwwww
                YYYYYwwwwwww
                YYYYYwwwwwww
                YYYYYwwwwwww
                YYYYYwwwwwww
                """
        def blobs = blobDetection.getBlobs(pixels)

        then:
        blobs.size() == 1
        with(blobs[0]) {
            width == 5
            height == 5
            x == 0
            y == 7
            color.color == Color.YELLOW
            !seen
        }
    }

    def "not quite but still seen"() {
        when:
        def pixels = pixelate """
                wwwwwwwwwwww
                wwwwwwwwwwww
                wwwwwwwwwwww
                wwwwwwwwwwww
                wwwwwwwwwwww
                wwwwwwwwwwww
                wwwwwwwwwwww
                YRYYwwwwwwww
                BYGYYwwwwwww
                YBYYawwwwwww
                eYYRYwwwwwww
                YwRYBwwwwwww
                """
        def blobs = blobDetection.getBlobs(pixels)

        then:
        blobs.size() == 1
        with(blobs[0]) {
            width == 4
            height == 4
            x == 1
            y == 7
            color.color == Color.YELLOW
            !seen
        }
    }

    def "oops"() {
        when:
        def pixels = pixelate """
                aaaaaaaaaaaaaaaaaa
                aaaaaaaaaaaaaaaaaa
                aaaaaaaaaaaaaaaaaa
                aaaaaaaaaaaaaaaaaa
                aaaaaaaaaaaaaaaaaa
                aaaaaaaaaaaaaaaaaa
                aaaaaaaaaaaaaaaaaa
                aaaaaaaaaaaaaaaaaa
                aaaaaaaaaaaaaaaaaa
                aaaaaaaaaaaaaaaaaa
                aaaaaaaaaaaaaaaaaa
                aaaaaaaaaaaaaaaaaa
                aaaaaaaaaaaaaaaaaa
                aaRRRYYYaaaaaaaaaa
                RRRYRRYYaaaaaaaaaa
                YRRRYYYRaaaaaaaaaa
                RRYYRRYYaaaaaaaaaa
                aaRRYYRRaaaaaaaaaa
                """
        def blobs = blobDetection.getBlobs(pixels)

        then:
        blobs.size() == 2
        with(blobs[0]) {
            width == 6
            height == 4
            x == 0
            y == 13
            color.color == Color.RED
            !seen
        }
        with(blobs[1]) {
            width == 4
            height == 4
            x == 4
            y == 13
            color.color == Color.YELLOW
            !seen
        }
    }

    def "gen"() {
        when:
        random.setSeed(0)
        1.upto(1000) {
            def a = (1..16).collect { (1..16).collect { pick() }.join() }.join("\n")
            def blobs = blobDetection.getBlobs(pixelate(a))
            if (!blobs.isEmpty()) {
                print a
                print "\n"
                //print blobs
                blobs.each {
                    println it.color.color
                    println it.x
                    println it.y
                    println it.width
                    println it.height
                    //print [ it.x, it.y, it.width, it.height ]
                }
                print "\n"
            }
        }

        then:
        true
    }

    def pick() {
        m.keySet()[random.nextInt(m.keySet().size())]
    }

    Pixel[][] pixelate(str) {
        str.stripMargin().split().collect { it.split("").collect { new Pixel(m[it]) } }
    }
}
