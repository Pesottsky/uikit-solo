export const copyToClipboard = (text, cb) => {
    try {
        navigator.clipboard.writeText(text);

        if (typeof cb === 'function') cb();
    } catch(e) {
        console.error(e);
    }
}