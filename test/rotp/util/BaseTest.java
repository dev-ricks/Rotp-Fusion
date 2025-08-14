package rotp.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BaseTest {

    Base base;

    @Mock
    DecimalFormat pad4;
    @Mock
    DecimalFormat sf8;
    @Mock
    DecimalFormat sf7;
    @Mock
    DecimalFormat sf6;
    @Mock
    DecimalFormat sf5;
    @Mock
    DecimalFormat sf4;
    @Mock
    DecimalFormat sf3;
    @Mock
    DecimalFormat sf2;
    @Mock
    DecimalFormat sf1;
    @Mock
    DecimalFormat df6;
    @Mock
    DecimalFormat df5;
    @Mock
    DecimalFormat df4;
    @Mock
    DecimalFormat df3;
    @Mock
    DecimalFormat df2;
    @Mock
    DecimalFormat df1;

    @BeforeEach
    void setUp() {
        base = new Base() {
        };
    }

    @Test
    void newBufferedImage_WithValidWidthAndHeight_ReturnsABufferedImageObject() {
        BufferedImage result = base.newBufferedImage(1, 1);
        assertNotNull(result);
    }

    @Test
    void newBufferedImage_WithInvalidWidthLessThanEqualToZero_DoesNotThrowIllegalArgumentException() {
        assertDoesNotThrow(() -> base.newBufferedImage(0, 1));
        assertDoesNotThrow(() -> base.newBufferedImage(-1, 1));
    }

    @Test
    void newBufferedImage_WithInvalidHeightLessThanEqualToZero_DoesNotThrowIllegalArgumentException() {
        assertDoesNotThrow(() -> base.newBufferedImage(1, 0));
        assertDoesNotThrow(() -> base.newBufferedImage(1, -1));
    }

    @Test
    void newBufferedImage_WithLessThanOrEqualToZeroWidthAndHeight_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> base.newBufferedImage(0, 0));
        assertThrows(IllegalArgumentException.class, () -> base.newBufferedImage(-1, -1));
    }

    @Test
    void newBufferedImage_WithWidthGreaterThanZeroAndHeightLessThanOrEqualToZero_ReturnsBufferedImageWithHeightSetToWidth() {
        int width = 127;
        BufferedImage result = base.newBufferedImage(width, 0);
        assertEquals(width, result.getHeight());
        result = base.newBufferedImage(width, -1);
        assertEquals(width, result.getHeight());
    }

    @Test
    void newBufferedImage_WithHeightGreaterThanZeroAndWidthLessThanOrEqualToZero_ReturnsBufferedImageWithWidthSetToHeight() {
        int height = 127;
        BufferedImage result = base.newBufferedImage(0, height);
        assertEquals(height, result.getWidth());
        result = base.newBufferedImage(-1, height);
        assertEquals(height, result.getWidth());
    }
}