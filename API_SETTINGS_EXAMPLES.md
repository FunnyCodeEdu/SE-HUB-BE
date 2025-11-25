# API Settings - Hướng dẫn Fetch

## Base URL
```
http://localhost:8080/api
```

## Authentication
Tất cả các API đều yêu cầu JWT token trong header:
```
Authorization: Bearer <your-jwt-token>
```

---

## 1. GET /profile/settings - Lấy tất cả settings

### cURL
```bash
curl -X GET "http://localhost:8080/api/profile/settings" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### JavaScript Fetch
```javascript
const getSettings = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/profile/settings', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${yourJwtToken}`,
        'Content-Type': 'application/json'
      }
    });
    
    const data = await response.json();
    console.log('Settings:', data);
    return data;
  } catch (error) {
    console.error('Error fetching settings:', error);
  }
};
```

### Axios
```javascript
import axios from 'axios';

const getSettings = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/profile/settings', {
      headers: {
        'Authorization': `Bearer ${yourJwtToken}`
      }
    });
    console.log('Settings:', response.data);
    return response.data;
  } catch (error) {
    console.error('Error:', error.response?.data || error.message);
  }
};
```

### Response Example
```json
{
  "code": "M005",
  "message": "Retrieved",
  "data": {
    "notificationSettings": {
      "emailEnabled": true,
      "pushEnabled": true,
      "mentionEnabled": true,
      "likeEnabled": true,
      "commentEnabled": true,
      "blogEnabled": true,
      "achievementEnabled": true,
      "followEnabled": true,
      "systemEnabled": true
    },
    "privacySettings": {
      "profilePublic": true,
      "emailVisible": false,
      "phoneVisible": true,
      "addressVisible": true,
      "dateOfBirthVisible": true,
      "majorVisible": true,
      "bioVisible": true,
      "socialMediaVisible": true,
      "achievementsVisible": true,
      "statsVisible": true
    }
  }
}
```

---

## 2. PUT /profile/settings - Cập nhật Settings (Notification + Privacy)

### cURL
```bash
curl -X PUT "http://localhost:8080/api/profile/settings" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "emailEnabled": true,
    "pushEnabled": false,
    "mentionEnabled": true,
    "likeEnabled": false,
    "commentEnabled": true,
    "blogEnabled": true,
    "achievementEnabled": true,
    "followEnabled": true,
    "systemEnabled": true,
    "profilePublic": false,
    "emailVisible": false,
    "phoneVisible": true,
    "addressVisible": false,
    "dateOfBirthVisible": true,
    "majorVisible": true,
    "bioVisible": true,
    "socialMediaVisible": false,
    "achievementsVisible": true,
    "statsVisible": true
  }'
```

### JavaScript Fetch
```javascript
const updateSettings = async (settings) => {
  try {
    const response = await fetch('http://localhost:8080/api/profile/settings', {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${yourJwtToken}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        // Notification settings (optional)
        emailEnabled: settings.emailEnabled,
        pushEnabled: settings.pushEnabled,
        mentionEnabled: settings.mentionEnabled,
        likeEnabled: settings.likeEnabled,
        commentEnabled: settings.commentEnabled,
        blogEnabled: settings.blogEnabled,
        achievementEnabled: settings.achievementEnabled,
        followEnabled: settings.followEnabled,
        systemEnabled: settings.systemEnabled,
        // Privacy settings (optional)
        profilePublic: settings.profilePublic,
        emailVisible: settings.emailVisible,
        phoneVisible: settings.phoneVisible,
        addressVisible: settings.addressVisible,
        dateOfBirthVisible: settings.dateOfBirthVisible,
        majorVisible: settings.majorVisible,
        bioVisible: settings.bioVisible,
        socialMediaVisible: settings.socialMediaVisible,
        achievementsVisible: settings.achievementsVisible,
        statsVisible: settings.statsVisible
      })
    });
    
    const data = await response.json();
    console.log('Updated settings:', data);
    return data;
  } catch (error) {
    console.error('Error updating settings:', error);
  }
};

// Example usage - Update cả notification và privacy
updateSettings({
  // Notification settings
  pushEnabled: false,      // Tắt push notification
  likeEnabled: false,      // Tắt thông báo like
  // Privacy settings
  profilePublic: false,    // Đặt trang cá nhân thành riêng tư
  emailVisible: false,      // Ẩn email
  socialMediaVisible: false // Ẩn mạng xã hội
});

// Example usage - Chỉ update notification settings
updateSettings({
  pushEnabled: false,
  likeEnabled: false
});

// Example usage - Chỉ update privacy settings
updateSettings({
  profilePublic: false,
  emailVisible: false
});
```

### Axios
```javascript
import axios from 'axios';

const updateSettings = async (settings) => {
  try {
    const response = await axios.put(
      'http://localhost:8080/api/profile/settings',
      {
        // Notification settings (optional)
        emailEnabled: settings.emailEnabled,
        pushEnabled: settings.pushEnabled,
        mentionEnabled: settings.mentionEnabled,
        likeEnabled: settings.likeEnabled,
        commentEnabled: settings.commentEnabled,
        blogEnabled: settings.blogEnabled,
        achievementEnabled: settings.achievementEnabled,
        followEnabled: settings.followEnabled,
        systemEnabled: settings.systemEnabled,
        // Privacy settings (optional)
        profilePublic: settings.profilePublic,
        emailVisible: settings.emailVisible,
        phoneVisible: settings.phoneVisible,
        addressVisible: settings.addressVisible,
        dateOfBirthVisible: settings.dateOfBirthVisible,
        majorVisible: settings.majorVisible,
        bioVisible: settings.bioVisible,
        socialMediaVisible: settings.socialMediaVisible,
        achievementsVisible: settings.achievementsVisible,
        statsVisible: settings.statsVisible
      },
      {
        headers: {
          'Authorization': `Bearer ${yourJwtToken}`
        }
      }
    );
    console.log('Updated:', response.data);
    return response.data;
  } catch (error) {
    console.error('Error:', error.response?.data || error.message);
  }
};
```

### Request Body (tất cả fields đều optional)
```json
{
  // Notification Settings
  "emailEnabled": true,        // Bật/tắt email notification
  "pushEnabled": true,         // Bật/tắt push notification (SSE)
  "mentionEnabled": true,       // Bật/tắt thông báo mention
  "likeEnabled": true,         // Bật/tắt thông báo like
  "commentEnabled": true,       // Bật/tắt thông báo comment
  "blogEnabled": true,         // Bật/tắt thông báo blog
  "achievementEnabled": true,  // Bật/tắt thông báo achievement
  "followEnabled": true,       // Bật/tắt thông báo follow
  "systemEnabled": true,       // Bật/tắt thông báo system
  
  // Privacy Settings
  "profilePublic": true,          // Trang cá nhân công khai/riêng tư
  "emailVisible": false,          // Bật/ẩn email (mặc định false)
  "phoneVisible": true,           // Bật/ẩn số điện thoại
  "addressVisible": true,         // Bật/ẩn địa chỉ
  "dateOfBirthVisible": true,     // Bật/ẩn ngày sinh
  "majorVisible": true,           // Bật/ẩn chuyên ngành
  "bioVisible": true,            // Bật/ẩn bio
  "socialMediaVisible": true,     // Bật/ẩn mạng xã hội
  "achievementsVisible": true,    // Bật/ẩn achievements
  "statsVisible": true           // Bật/ẩn stats
}
```

### Response Example
```json
{
  "code": "M003",
  "message": "Updated",
  "data": {
    "notificationSettings": {
      "emailEnabled": true,
      "pushEnabled": false,
      "mentionEnabled": true,
      "likeEnabled": false,
      "commentEnabled": true,
      "blogEnabled": true,
      "achievementEnabled": true,
      "followEnabled": true,
      "systemEnabled": true
    },
    "privacySettings": {
      "profilePublic": false,
      "emailVisible": false,
      "phoneVisible": true,
      "addressVisible": false,
      "dateOfBirthVisible": true,
      "majorVisible": true,
      "bioVisible": true,
      "socialMediaVisible": false,
      "achievementsVisible": true,
      "statsVisible": true
    }
  }
}
```

---

## React Hook Example

```javascript
import { useState, useEffect } from 'react';
import axios from 'axios';

const useSettings = (token) => {
  const [settings, setSettings] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchSettings = async () => {
    try {
      setLoading(true);
      const response = await axios.get(
        'http://localhost:8080/api/profile/settings',
        {
          headers: { 'Authorization': `Bearer ${token}` }
        }
      );
      setSettings(response.data.data);
      setError(null);
    } catch (err) {
      setError(err.response?.data || err.message);
    } finally {
      setLoading(false);
    }
  };

  const updateSettings = async (settingsData) => {
    try {
      const response = await axios.put(
        'http://localhost:8080/api/profile/settings',
        settingsData,
        {
          headers: { 'Authorization': `Bearer ${token}` }
        }
      );
      setSettings(response.data.data);
      return response.data;
    } catch (err) {
      setError(err.response?.data || err.message);
      throw err;
    }
  };

  useEffect(() => {
    if (token) {
      fetchSettings();
    }
  }, [token]);

  return {
    settings,
    loading,
    error,
    refetch: fetchSettings,
    updateSettings
  };
};

export default useSettings;
```

---

## Vue.js Composable Example

```javascript
import { ref, onMounted } from 'vue';
import axios from 'axios';

export const useSettings = (token) => {
  const settings = ref(null);
  const loading = ref(true);
  const error = ref(null);

  const fetchSettings = async () => {
    try {
      loading.value = true;
      const response = await axios.get(
        'http://localhost:8080/api/profile/settings',
        {
          headers: { 'Authorization': `Bearer ${token}` }
        }
      );
      settings.value = response.data.data;
      error.value = null;
    } catch (err) {
      error.value = err.response?.data || err.message;
    } finally {
      loading.value = false;
    }
  };

  const updateSettings = async (settingsData) => {
    try {
      const response = await axios.put(
        'http://localhost:8080/api/profile/settings',
        settingsData,
        {
          headers: { 'Authorization': `Bearer ${token}` }
        }
      );
      settings.value = response.data.data;
      return response.data;
    } catch (err) {
      error.value = err.response?.data || err.message;
      throw err;
    }
  };

  onMounted(() => {
    if (token) {
      fetchSettings();
    }
  });

  return {
    settings,
    loading,
    error,
    fetchSettings,
    updateSettings
  };
};
```

---

## Lưu ý

1. **Tất cả các fields trong request body đều là optional** - Bạn chỉ cần gửi các field muốn update
2. **Có thể update riêng notification hoặc privacy hoặc cả 2 cùng lúc** - API sẽ tự động detect và chỉ update những field được gửi
3. **Mặc định values:**
   - Notification: Tất cả đều `true` (bật)
   - Privacy: `profilePublic = true`, `emailVisible = false`, còn lại `true`
4. **Khi `profilePublic = false`**: Trang cá nhân sẽ là riêng tư, chỉ owner mới xem được
5. **Khi `pushEnabled = false`**: Notification vẫn được lưu vào DB nhưng không push qua SSE/Redis
6. **Khi các notification type = false**: Notification đó sẽ không được tạo
