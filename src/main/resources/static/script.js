// JavaScript for slider functionality
const sliderWrapper = document.querySelector('.slider-wrapper');
const prevBtn = document.querySelector('.prev');
const nextBtn = document.querySelector('.next');

let slideInterval; // Variable to store the interval

// Function to start the slide interval
function startSlideInterval() {
    slideInterval = setInterval(() => {
        sliderWrapper.appendChild(sliderWrapper.firstElementChild);
    }, 5000); // 5 seconds
}

// Function to stop the slide interval
function stopSlideInterval() {
    clearInterval(slideInterval);
}

// Start the slide interval on page load
startSlideInterval();

// Function to slide to the previous slide
prevBtn.addEventListener('click', () => {
    sliderWrapper.appendChild(sliderWrapper.firstElementChild);
});

// Function to slide to the next slide
nextBtn.addEventListener('click', () => {
    sliderWrapper.insertBefore(sliderWrapper.lastElementChild, sliderWrapper.firstElementChild);
});