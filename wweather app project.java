<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>Expandable Weather App</title>
<style>
  /* Reset and base */
  * {
    box-sizing: border-box;
    margin: 0;
    padding: 0;
  }
  body {
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    background: linear-gradient(135deg, #6dd5ed, #2193b0);
    color: #333;
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 1rem;
    transition: background 0.5s ease;
  }
  body.dark-theme {
    background: linear-gradient(135deg, #232526, #1c1c1c);
    color: #ddd;
  }

  h1 {
    margin-bottom: 1rem;
    text-shadow: 1px 1px #0003;
  }

  .app-container {
    background: rgba(255, 255, 255, 0.85);
    border-radius: 1rem;
    padding: 1.5rem;
    max-width: 700px;
    width: 100%;
    box-shadow: 0 0 15px rgba(0,0,0,0.2);
    transition: background 0.5s ease, color 0.5s ease;
  }

  body.dark-theme .app-container {
    background: rgba(30,30,30,0.9);
    color: #ddd;
  }

  .search-section {
    display: flex;
    gap: 0.75rem;
    margin-bottom: 1.5rem;
  }
  .search-section input[type="text"] {
    flex-grow: 1;
    padding: 0.65rem 1rem;
    border-radius: 0.5rem;
    border: 1px solid #aaa;
    font-size: 1rem;
    transition: border-color 0.3s ease;
  }
  body.dark-theme .search-section input[type="text"] {
    background: #232323;
    border-color: #555;
    color: #ddd;
  }
  .search-section input[type="text"]:focus {
    border-color: #2196f3;
    outline: none;
  }
  .search-section button {
    padding: 0.65rem 1.25rem;
    border: none;
    background-color: #2196f3;
    color: white;
    border-radius: 0.5rem;
    font-weight: 600;
    cursor: pointer;
    transition: background 0.3s ease;
  }
  .search-section button:hover {
    background-color: #0b7dda;
  }

  .options-section {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1rem;
    gap: 1rem;
  }
  .toggle-group {
    display: flex;
    gap: 0.5rem;
    align-items: center;
  }
  label {
    font-size: 0.9rem;
  }
  select, input[type="checkbox"] {
    cursor: pointer;
  }

  /* Weather Display Sections */
  .current-weather {
    text-align: center;
    margin-bottom: 1.5rem;
  }
  .current-weather h2 {
    font-size: 2rem;
    margin-bottom: 0.25rem;
  }
  .current-weather .temp {
    font-size: 3.5rem;
    font-weight: 700;
    margin-bottom: 0.25rem;
  }
  .current-weather .desc {
    font-style: italic;
    margin-bottom: 0.5rem;
  }
  .current-weather .extra-info {
    display: flex;
    justify-content: center;
    gap: 2rem;
    font-size: 0.9rem;
    color: #555;
  }
  body.dark-theme .current-weather .extra-info {
    color: #bbb;
  }

  /* Forecast Section */
  .forecast {
    display: flex;
    justify-content: space-between;
    gap: 0.75rem;
    overflow-x: auto;
  }
  .forecast-day {
    background: #e1f5fe;
    border-radius: 0.75rem;
    padding: 1rem;
    flex: 1 1 100px;
    text-align: center;
    box-shadow: 0 0 5px #81d4fa;
    transition: background 0.3s ease;
  }
  body.dark-theme .forecast-day {
    background: #444;
    box-shadow: 0 0 5px #2196f3;
    color: #ddd;
  }
  .forecast-day h3 {
    margin-bottom: 0.25rem;
  }
  .forecast-day img {
    width: 50px;
    height: 50px;
    margin-bottom: 0.25rem;
  }
  .forecast-day .temp-range {
    font-weight: 600;
    margin-bottom: 0.25rem;
  }
  .forecast-day .desc {
    font-style: italic;
  }

  /* Responsive */
  @media (max-width: 600px) {
    .forecast {
      flex-direction: column;
      gap: 1rem;
    }
  }

  /* Loading and error message */
  .message {
    text-align: center;
    font-size: 1.1rem;
    color: #b00020;
    margin-top: 1rem;
  }

  /* Footer */
  footer {
    margin-top: 2rem;
    font-size: 0.8rem;
    color: #666;
    text-align: center;
  }
  body.dark-theme footer {
    color: #aaa;
  }
</style>
</head>
<body>
  <h1>Expandable Weather App</h1>
  <div class="app-container" role="main">
    <section class="search-section" aria-label="Search Weather">
      <input type="text" id="city-input" aria-label="Enter city name" placeholder="Enter city name, e.g. New York" />
      <button id="search-btn" aria-label="Search weather">Search</button>
    </section>

    <section class="options-section" aria-label="Options">
      <div class="toggle-group">
        <label for="unit-select">Units:</label>
        <select id="unit-select" aria-label="Select temperature unit">
          <option value="metric">Celsius (°C)</option>
          <option value="imperial">Fahrenheit (°F)</option>
        </select>
      </div>
      <div class="toggle-group">
        <label for="theme-toggle">Dark Theme:</label>
        <input type="checkbox" id="theme-toggle" aria-checked="false" />
      </div>
    </section>

    <section class="current-weather" aria-live="polite" aria-atomic="true" aria-label="Current weather">
      </section>

    <section class="forecast" aria-label="Weather forecast for next days" tabindex="0">
      </section>

    <div class="message" role="alert" aria-live="assertive"></div>
  </div>

  <footer>
    &copy; 2024 Expandable Weather App. Data provided by OpenWeatherMap API.
  </footer>

<script>
(() => {
  // Constants and Configurations
  // IMPORTANT: Replace 'YOUR_OPENWEATHERMAP_API_KEY' with your actual API key
  // You can get one for free from https://openweathermap.org/api
  const API_KEY = 'YOUR_OPENWEATHERMAP_API_KEY'; 
  const API_BASE = 'https://api.openweathermap.org/data/2.5';

  // Selectors
  const cityInput = document.getElementById('city-input');
  const searchBtn = document.getElementById('search-btn');
  const currentWeatherSection = document.querySelector('.current-weather');
  const forecastSection = document.querySelector('.forecast');
  const unitSelect = document.getElementById('unit-select');
  const themeToggle = document.getElementById('theme-toggle');
  const messageDiv = document.querySelector('.message');

  // State variables
  let currentUnit = 'metric'; // metric or imperial

  // Helper Functions
  function capitalizeWords(str) {
    return str.replace(/\b\w/g, (c) => c.toUpperCase());
  }

  function setMessage(msg, isError = false) {
    messageDiv.textContent = msg;
    if (isError) {
      messageDiv.style.color = '#b00020'; // Error color
    } else {
      messageDiv.style.color = ''; // Reset color for other messages
    }
  }

  function clearMessage() {
    messageDiv.textContent = '';
  }

  // Weather Icon URL helper from OpenWeatherMap
  function getIconUrl(iconCode) {
    return `https://openweathermap.org/img/wn/${iconCode}@2x.png`;
  }

  // Fetch data helpers
  async function fetchCurrentWeather(city, units) {
    if (API_KEY === 'YOUR_OPENWEATHERMAP_API_KEY' || !API_KEY) {
      throw new Error('API Key is not set. Please replace "YOUR_OPENWEATHERMAP_API_KEY" with your actual OpenWeatherMap API key.');
    }
    const url = `${API_BASE}/weather?q=${encodeURIComponent(city)}&units=${units}&appid=${API_KEY}`;
    const res = await fetch(url);
    if (!res.ok) {
      // More specific error messages based on status
      if (res.status === 404) {
        throw new Error('City not found. Please check the spelling or try another city.');
      } else if (res.status === 401) {
        throw new Error('Invalid API Key. Please ensure your OpenWeatherMap API key is correct and active.');
      } else {
        throw new Error(`Failed to fetch current weather: ${res.statusText}`);
      }
    }
    return await res.json();
  }

  async function fetchForecast(lat, lon, units) {
    if (API_KEY === 'YOUR_OPENWEATHERMAP_API_KEY' || !API_KEY) {
      throw new Error('API Key is not set. Please replace "YOUR_OPENWEATHERMAP_API_KEY" with your actual OpenWeatherMap API key.');
    }
    // Using One Call API for daily forecast
    const url = `${API_BASE}/onecall?lat=${lat}&lon=${lon}&exclude=current,minutely,hourly,alerts&units=${units}&appid=${API_KEY}`;
    const res = await fetch(url);
    if (!res.ok) {
      throw new Error(`Failed to fetch forecast: ${res.statusText}`);
    }
    return await res.json();
  }

  // UI update functions
  function renderCurrentWeather(data, units) {
    const tempUnit = units === 'metric' ? '°C' : '°F';
    const weather = data.weather[0];
    const sunriseDate = new Date(data.sys.sunrise * 1000);
    const sunsetDate = new Date(data.sys.sunset * 1000);

    currentWeatherSection.innerHTML = `
      <h2>${capitalizeWords(data.name)}, ${data.sys.country}</h2>
      <div class="temp">${Math.round(data.main.temp)}${tempUnit}</div>
      <div class="desc">${capitalizeWords(weather.description)}</div>
      <img src="${getIconUrl(weather.icon)}" alt="${capitalizeWords(weather.description)} icon" />
      <div class="extra-info">
        <div>Humidity: ${data.main.humidity}%</div>
        <div>Wind: ${Math.round(data.wind.speed)} ${units === 'metric' ? 'm/s' : 'mph'}</div>
        <div>Sunrise: ${sunriseDate.toLocaleTimeString()}</div>
        <div>Sunset: ${sunsetDate.toLocaleTimeString()}</div>
      </div>
    `;
  }

  function renderForecast(data, units) {
    const tempUnit = units === 'metric' ? '°C' : '°F';
    // Show next 7 days forecast excluding current day
    forecastSection.innerHTML = '';
    if (data.daily && data.daily.length > 1) { // Ensure daily data exists and has more than just today
      data.daily.slice(1, 8).forEach((day) => { // Slice from index 1 to get next 7 days
        const dayDate = new Date(day.dt * 1000);
        const dayName = dayDate.toLocaleDateString(undefined, { weekday: 'short' });
        const icon = getIconUrl(day.weather[0].icon);
        const desc = capitalizeWords(day.weather[0].description);
        const tempRange = `${Math.round(day.temp.min)}${tempUnit} / ${Math.round(day.temp.max)}${tempUnit}`;

        const dayElem = document.createElement('article');
        dayElem.className = 'forecast-day';
        dayElem.setAttribute('tabindex', '0');
        dayElem.innerHTML = `
          <h3>${dayName}</h3>
          <img src="${icon}" alt="${desc} icon" />
          <div class="temp-range">${tempRange}</div>
          <div class="desc">${desc}</div>
        `;
        forecastSection.appendChild(dayElem);
      });
    } else {
      forecastSection.innerHTML = '<p style="text-align: center; width: 100%;">No forecast data available.</p>';
    }
  }

  // Main search function
  async function searchWeather() {
    clearMessage();
    const city = cityInput.value.trim();
    if (!city) {
      setMessage('Please enter a city name.', true);
      return;
    }
    try {
      setMessage('Loading weather data...');
      const currentData = await fetchCurrentWeather(city, currentUnit);
      renderCurrentWeather(currentData, currentUnit);
      
      // Ensure we have coordinates for forecast
      if (currentData.coord && currentData.coord.lat && currentData.coord.lon) {
        const forecastData = await fetchForecast(currentData.coord.lat, currentData.coord.lon, currentUnit);
        renderForecast(forecastData, currentUnit);
      } else {
        setMessage('Could not retrieve coordinates for forecast.', true);
        forecastSection.innerHTML = ''; // Clear any previous forecast
      }
      clearMessage(); // Clear message on success
    } catch (err) {
      currentWeatherSection.innerHTML = '';
      forecastSection.innerHTML = '';
      setMessage('Error: ' + err.message, true);
    }
  }

  // Event Listeners
  searchBtn.addEventListener('click', () => {
    searchWeather();
  });

  cityInput.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
      searchWeather();
    }
  });

  unitSelect.addEventListener('change', () => {
    currentUnit = unitSelect.value;
    if (cityInput.value.trim()) {
      searchWeather(); // refresh data with new units
    }
  });

  themeToggle.addEventListener('change', () => {
    const checked = themeToggle.checked;
    document.body.classList.toggle('dark-theme', checked);
    themeToggle.setAttribute('aria-checked', checked.toString());
    // Store theme preference in localStorage
    localStorage.setItem('darkTheme', checked);
  });

  // Initialization function
  function initializeApp() {
    // Load theme preference from localStorage
    const savedTheme = localStorage.getItem('darkTheme');
    if (savedTheme === 'true') {
      document.body.classList.add('dark-theme');
      themeToggle.checked = true;
      themeToggle.setAttribute('aria-checked', 'true');
    }

    // Load default city weather on startup
    cityInput.value = 'New York'; // Set a default city
    searchWeather();
  }

  // Call initialization function when the DOM is fully loaded
  document.addEventListener('DOMContentLoaded', initializeApp);

})();
</script>
</body>
</html>
