const CURRENCIES = {
    INR: {
        code: "INR",
        label: "Indian Rupee (₹ INR)",
        locale: "en-IN",
        symbol: "₹",
    },
    USD: {
        code: "USD",
        label: "US Dollar ($ USD)",
        locale: "en-US",
        symbol: "$",
    },
    EUR: {
        code: "EUR",
        label: "Euro (€ EUR)",
        locale: "de-DE",
        symbol: "€",
    },
    GBP: {
        code: "GBP",
        label: "British Pound (£ GBP)",
        locale: "en-GB",
        symbol: "£",
    },
};

export const getCurrency = () => {
    const savedCurrency = localStorage.getItem("currency");

    return CURRENCIES[savedCurrency] || CURRENCIES.INR;
};

export const formatCurrency = (amount) => {
    const currency = getCurrency();

    return new Intl.NumberFormat(currency.locale, {
        style: "currency",
        currency: currency.code,
        maximumFractionDigits: 2,
    }).format(Number(amount) || 0);
};

export default CURRENCIES;