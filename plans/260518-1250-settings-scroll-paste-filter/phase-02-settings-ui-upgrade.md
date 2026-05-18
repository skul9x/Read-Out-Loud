# Phase 02: Nâng Cấp Toàn Diện UI/UX Settings (Khắc Phục 4 Lỗi Thực Tế Từ Thiết Bị) - [🚀 COMPLETED]

## 🎯 Gợi ý kỹ thuật nâng cao từ Google & Cộng đồng Android
*   **Tránh xung đột cuộn (Scroll Conflict):** Khi đặt một `TextInputEditText` nhiều dòng (Text Area) có khả năng cuộn bên trong một `NestedScrollView`, nếu nội dung API Keys quá dài, việc vuốt của người dùng sẽ bị `NestedScrollView` đánh chặn, khiến cả màn hình bị cuộn thay vì cuộn nội dung bên trong ô nhập.
*   **Giải pháp chống ngắt dòng chữ (Anti-Text-Wrapping):** Với các tên biến hoặc tên model chứa ký tự đặc biệt (như dấu gạch ngang `-`), Android sẽ tự động ngắt dòng nếu `layout_width` là `wrap_content`. Giải pháp duy nhất bền vững là đặt `layout_width="match_parent"` hoặc `0dp/weight=1` kết hợp khống chế dòng cứng `maxLines="1"` và `ellipsize="end"`.
*   **Khoảng cách vùng chạm (Touch Target Spacing):** Theo Google Material Design 3, các nút tương tác liền kề cần có margin giãn cách (tối thiểu 4dp-8dp) để tránh hiện tượng người dùng nhấn nhầm nút, đặc biệt là nút Xoá nằm ngay cạnh nút tăng/giảm ưu tiên.

---

## 🎯 Mục tiêu & Các lỗi được khắc phục trực tiếp

1.  **[x] 🔴 Sửa lỗi biến mất ô nhập API Key (Vấn đề 1):** Thay đổi chiều cao của Card chứa API Key (`MaterialCardView` chứa `apiKeyEditText`) thành chiều cao cố định **`180dp`** thay vì `0dp`/`weight="1"` để ngăn chặn việc bị co rúm thành 0px khi đo đạc layout.
2.  **[x] 🔴 Sửa lỗi biến mất nút lưu "SAVE CONFIGURATION" (Vấn đề 4):** Bọc toàn bộ nội dung cài đặt dưới thanh Toolbar bằng `NestedScrollView` và di chuyển nút Save vào dưới cùng vùng cuộn. Giúp người dùng cuộn nhẹ màn hình là bấm lưu dễ dàng.
3.  **[x] 🔴 Sửa lỗi ngắt chữ Model xấu xí (Vấn đề 2):** Cập nhật `item_model.xml` thiết lập TextView hiển thị tên model (`modelNameText`) có chiều rộng chiếm hết vùng chứa (`match_parent`), khống chế chỉ hiển thị trên một dòng duy nhất (`maxLines="1"`, `singleLine="true"`) và có dấu ba chấm chuyên nghiệp khi quá dài (`ellipsize="end"`).
4.  **[x] 🟡 Sửa lỗi Touch Target nút tăng/giảm/xóa quá bé và sát nhau (Vấn đề 3):** Thiết lập khoảng cách giãn cách ngang (horizontal margins) hợp lý giữa các nút điều hướng và nút xóa trong `item_model.xml` để người dùng không bấm nhầm, đồng thời đảm bảo vùng touch target tối thiểu luôn đạt chuẩn `48dp` của Google.
5.  **[x] 🟢 Ngăn chặn xung đột cuộn mượt mà:** Triển khai cơ chế disallow intercept touch event cho `apiKeyEditText` trong `SettingsActivity.kt` để người dùng cuộn nội dung bên trong Text Area mà không bị NestedScrollView đánh chặn cuộn cả màn hình.

---

## 🛠️ Các file cần chỉnh sửa & tạo mới

### 1. File sửa đổi: `app/src/main/res/layout/activity_settings.xml`
*   **Chi tiết thay đổi:**
    *   Bọc toàn bộ nội dung nằm dưới `MaterialToolbar` bằng `androidx.core.widget.NestedScrollView` để hỗ trợ cuộn dọc.
    *   Bên trong `NestedScrollView`, sử dụng một `LinearLayout` (orientation="vertical") làm container chứa toàn bộ các card/view cài đặt.
    *   Thay đổi `MaterialCardView` chứa `apiKeyEditText` từ `android:layout_height="0dp"` và `android:layout_weight="1"` thành `android:layout_height="180dp"` và đặt ID là `android:id="@+id/apiKeyCard"`.
    *   Bổ sung thuộc tính `android:minLines="5"`, `android:gravity="top|start"` cho `TextInputEditText` có ID `apiKeyEditText`.
    *   Di chuyển nút `saveButton` (SAVE CONFIGURATION) vào trong `NestedScrollView` ở dưới cùng để người dùng cuộn xuống dưới là thấy nút lưu.

### 2. File sửa đổi: `app/src/main/res/layout/item_model.xml`
*   **Chi tiết thay đổi:**
    *   Cập nhật TextView `@id/modelNameText` từ `android:layout_width="wrap_content"` thành `android:layout_width="match_parent"`.
    *   Bổ sung thuộc tính `android:maxLines="1"`, `android:singleLine="true"`, và `android:ellipsize="end"` cho TextView `@id/modelNameText`.
    *   Bổ sung `android:layout_marginStart="6dp"`, `android:layout_marginEnd="6dp"` cho các `ImageButton` `@id/moveUpButton`, `@id/moveDownButton`, và `@id/deleteButton` để tạo khoảng trống trực quan rộng rãi, tránh bấm nhầm.
    *   Bổ sung `android:contentDescription` cho từng `ImageButton` để hỗ trợ tính năng tiếp cận của Google (Accessibility).

### 3. File sửa đổi: `app/src/main/java/com/skul9x/readoutloud/ui/SettingsActivity.kt`
*   **Chi tiết thay đổi:**
    *   Trong hàm `setupUI()`, bổ sung thiết lập `OnTouchListener` cho `binding.apiKeyEditText` để giải quyết xung đột cuộn dọc:
        ```kotlin
        binding.apiKeyEditText.setOnTouchListener { view, event ->
            if (view.id == R.id.apiKeyEditText) {
                view.parent.requestDisallowInterceptTouchEvent(true)
                if ((event.action and android.view.MotionEvent.ACTION_MASK) == android.view.MotionEvent.ACTION_UP) {
                    view.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }
        ```

### 4. File tạo mới: `app/src/test/java/com/skul9x/readoutloud/ui/SettingsActivityUiLayoutTest.kt`
*   **Mục đích:** Đảm bảo toàn bộ 4 lỗi UI/UX cài đặt được khắc phục triệt để và được viết thành bộ test tự động để tránh tái diễn lỗi về sau (Regression Testing).
*   **Công nghệ sử dụng:** Robolectric Test Runner, JUnit 4.

---

## 💻 Chi tiết thay đổi bố cục trong `item_model.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:gravity="center_vertical"
    android:orientation="horizontal"
    android:padding="8dp">

    <CheckBox
        android:id="@+id/modelCheckBox"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />

    <LinearLayout
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:orientation="vertical"
        android:paddingStart="8dp"
        android:paddingEnd="8dp">

        <!-- Sửa lỗi ngắt dòng model: chiều rộng match_parent, maxLines 1, ellipsize end -->
        <TextView
            android:id="@+id/modelNameText"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:maxLines="1"
            android:singleLine="true"
            android:ellipsize="end"
            android:textAppearance="@style/TextAppearance.Material3.BodyLarge"
            android:textColor="?attr/colorOnSurface" />

        <TextView
            android:id="@+id/modelStatusText"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textAppearance="@style/TextAppearance.Material3.LabelSmall"
            android:textColor="?attr/colorOnSurfaceVariant" />
    </LinearLayout>

    <!-- Thêm margins giãn cách (6dp) cho các nút hành động để tăng khoảng trống trực quan và cải thiện Touch Target -->
    <ImageButton
        android:id="@+id/moveUpButton"
        android:layout_width="48dp"
        android:layout_height="48dp"
        android:layout_marginStart="6dp"
        android:layout_marginEnd="6dp"
        android:background="?attr/selectableItemBackgroundBorderless"
        android:src="@drawable/ic_arrow_up"
        android:contentDescription="Move model up in priority"
        app:tint="?attr/colorOnSurfaceVariant" />

    <ImageButton
        android:id="@+id/moveDownButton"
        android:layout_width="48dp"
        android:layout_height="48dp"
        android:layout_marginStart="6dp"
        android:layout_marginEnd="6dp"
        android:background="?attr/selectableItemBackgroundBorderless"
        android:src="@drawable/ic_arrow_down"
        android:contentDescription="Move model down in priority"
        app:tint="?attr/colorOnSurfaceVariant" />

    <ImageButton
        android:id="@+id/deleteButton"
        android:layout_width="48dp"
        android:layout_height="48dp"
        android:layout_marginStart="6dp"
        android:layout_marginEnd="6dp"
        android:background="?attr/selectableItemBackgroundBorderless"
        android:src="@drawable/ic_delete"
        android:contentDescription="Delete model"
        app:tint="?attr/colorError" />

</LinearLayout>
```

---

## 💻 Mã nguồn tệp kiểm thử tự động toàn diện (`SettingsActivityUiLayoutTest.kt`)

```kotlin
package com.skul9x.readoutloud.ui

import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import com.google.android.material.card.MaterialCardView
import com.skul9x.readoutloud.R
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SettingsActivityUiLayoutTest {

    @Before
    fun setUp() {
        // Khởi tạo trạng thái môi trường test nếu cần
    }

    @Test
    fun testSettingsLayoutContainsNestedScrollView() {
        val activity = Robolectric.buildActivity(SettingsActivity::class.java).setup().get()

        var foundScrollView = false
        val root = activity.findViewById<android.view.View>(android.R.id.content) as android.view.ViewGroup
        
        fun searchForNestedScrollView(view: android.view.View) {
            if (view is NestedScrollView) {
                foundScrollView = true
                return
            }
            if (view is android.view.ViewGroup) {
                for (i in 0 until view.childCount) {
                    searchForNestedScrollView(view.getChildAt(i))
                }
            }
        }
        searchForNestedScrollView(root)

        assertTrue("Settings layout should contain a NestedScrollView to support vertical scrolling", foundScrollView)
    }

    @Test
    fun testApiKeyInputCardHasFixedTextAreaHeight() {
        val activity = Robolectric.buildActivity(SettingsActivity::class.java).setup().get()
        
        // Tìm Card chứa API Key qua ID
        val cardView = activity.findViewById<MaterialCardView>(R.id.apiKeyCard)
        assertNotNull("MaterialCardView holding the apiKeyEditText should exist", cardView)
        
        val layoutParams = cardView.layoutParams
        assertNotNull("Layout params should not be null", layoutParams)
        
        // Quy đổi 180dp thành pixel tương ứng theo density để test chạy mượt trên mọi môi trường
        val density = activity.resources.displayMetrics.density
        val expectedHeightPx = (180 * density).toInt()
        
        assertEquals("Card height should be set to 180dp", expectedHeightPx, layoutParams.height)
    }

    @Test
    fun testApiKeyEditTextPropertiesAsTextArea() {
        val activity = Robolectric.buildActivity(SettingsActivity::class.java).setup().get()
        val apiKeyEditText = activity.findViewById<EditText>(R.id.apiKeyEditText)
        
        assertNotNull("apiKeyEditText should exist", apiKeyEditText)
        
        assertEquals("EditText minLines should be set to 5", 5, apiKeyEditText.minLines)
        assertEquals("EditText gravity should be set to top|start", 
            android.view.Gravity.TOP or android.view.Gravity.START, 
            apiKeyEditText.gravity and (android.view.Gravity.TOP or android.view.Gravity.START)
        )
    }

    @Test
    fun testApiKeyEditTextTouchListenerAssigned() {
        val activity = Robolectric.buildActivity(SettingsActivity::class.java).setup().get()
        val apiKeyEditText = activity.findViewById<EditText>(R.id.apiKeyEditText)
        
        assertNotNull("apiKeyEditText should exist", apiKeyEditText)
        
        val downEvent = android.view.MotionEvent.obtain(0, 0, android.view.MotionEvent.ACTION_DOWN, 0f, 0f, 0)
        val handled = apiKeyEditText.dispatchTouchEvent(downEvent)
        downEvent.recycle()
        
        assertFalse("dispatchTouchEvent for custom touch intercept should pass event down", handled)
    }

    @Test
    fun testModelItemViewPropertiesAvoidTextWrapping() {
        val activity = Robolectric.buildActivity(SettingsActivity::class.java).setup().get()
        val inflater = activity.layoutInflater
        val itemView = inflater.inflate(R.layout.item_model, null)
        
        val modelNameText = itemView.findViewById<TextView>(R.id.modelNameText)
        assertNotNull("modelNameText should exist in item_model layout", modelNameText)
        
        // Khẳng định chống lỗi ngắt chữ model
        assertEquals("modelNameText maxLines should be set to 1 to avoid text wrapping bug", 1, modelNameText.maxLines)
        assertEquals("modelNameText ellipsize should be set to END to display trailing dots professionally", 
            android.text.TextUtils.TruncateAt.END, modelNameText.ellipsize)
    }

    @Test
    fun testModelItemActionButtonsTouchTargetAndSpacing() {
        val activity = Robolectric.buildActivity(SettingsActivity::class.java).setup().get()
        val inflater = activity.layoutInflater
        val itemView = inflater.inflate(R.layout.item_model, null)
        
        val moveUpButton = itemView.findViewById<ImageButton>(R.id.moveUpButton)
        val moveDownButton = itemView.findViewById<ImageButton>(R.id.moveDownButton)
        val deleteButton = itemView.findViewById<ImageButton>(R.id.deleteButton)
        
        assertNotNull(moveUpButton)
        assertNotNull(moveDownButton)
        assertNotNull(deleteButton)
        
        val density = activity.resources.displayMetrics.density
        val minTouchTargetPx = (48 * density).toInt()
        
        // Khẳng định kích thước vùng chạm tối thiểu là 48dp đạt chuẩn Accessibility của Google
        assertTrue("moveUpButton width should be at least 48dp", moveUpButton.layoutParams.width >= minTouchTargetPx)
        assertTrue("moveUpButton height should be at least 48dp", moveUpButton.layoutParams.height >= minTouchTargetPx)
        
        // Khẳng định các nút có margins giãn cách ngang để tránh chạm nhầm
        val upParams = moveUpButton.layoutParams as? android.view.ViewGroup.MarginLayoutParams
        val downParams = moveDownButton.layoutParams as? android.view.ViewGroup.MarginLayoutParams
        
        assertNotNull("Layout params should be MarginLayoutParams", upParams)
        assertNotNull("Layout params should be MarginLayoutParams", downParams)
        
        assertTrue("moveUpButton should have margin to separate from checkbox and adjacent buttons", 
            upParams!!.leftMargin > 0 || upParams.rightMargin > 0)
        assertTrue("moveDownButton should have margin to separate from adjacent buttons", 
            downParams!!.leftMargin > 0 || downParams.rightMargin > 0)
    }
}
```

---

## 📋 Các bước thực hiện cụ thể
1.  **Bước 1:** Thay đổi toàn bộ nội dung file `app/src/main/res/layout/activity_settings.xml` sang cấu trúc mới có chứa `NestedScrollView`, chiều cao Card cố định `180dp` và nút Save ở trong scroll view dưới cùng.
2.  **Bước 2:** Cập nhật file `app/src/main/res/layout/item_model.xml` để sửa lỗi ngắt chữ model (TextView) và thêm margins giãn cách ngang (6dp) cho các ImageButton.
3.  **Bước 3:** Chỉnh sửa logic trong file `SettingsActivity.kt` tại hàm `setupUI()` để gắn bộ lắng nghe chạm `OnTouchListener` tránh xung đột cuộn.
4.  **Bước 4:** Tạo file test `app/src/test/java/com/skul9x/readoutloud/ui/SettingsActivityUiLayoutTest.kt` với mã nguồn kiểm thử trên.
5.  **Bước 5:** Chạy bộ test để xác thực giao diện mới đã sửa hoàn hảo tất cả các lỗi UI/UX và vận hành trơn tru.
