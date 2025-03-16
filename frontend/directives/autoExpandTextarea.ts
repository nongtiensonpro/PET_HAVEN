export default {
  mounted(el: HTMLTextAreaElement) {
    el.style.overflow = 'hidden';
    el.style.minHeight = '2rem';
    
    const resize = () => {
      el.style.height = 'auto';
      el.style.height = el.scrollHeight + 'px';
    };

    el.addEventListener('input', resize);
    resize();
  }
};