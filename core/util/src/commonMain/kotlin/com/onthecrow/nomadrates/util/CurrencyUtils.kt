package com.onthecrow.nomadrates.util

expect fun String.toCurrencyName(): String

fun String.toIsoCountryCode(): String? {
    return when (this.uppercase()) {
        // Standard Mappings
        "AED" -> "AE" // United Arab Emirates
        "AFN" -> "AF" // Afghanistan
        "ALL" -> "AL" // Albania
        "AMD" -> "AM" // Armenia
        "ANG" -> "CW" // Curaçao (Netherlands Antilles used AN, but it's deprecated)
        "AOA" -> "AO" // Angola
        "ARS" -> "AR" // Argentina
        "AUD" -> "AU" // Australia
        "AWG" -> "AW" // Aruba
        "AZN" -> "AZ" // Azerbaijan
        "BAM" -> "BA" // Bosnia and Herzegovina
        "BBD" -> "BB" // Barbados
        "BDT" -> "BD" // Bangladesh
        "BGN" -> "BG" // Bulgaria
        "BHD" -> "BH" // Bahrain
        "BIF" -> "BI" // Burundi
        "BMD" -> "BM" // Bermuda
        "BND" -> "BN" // Brunei
        "BOB" -> "BO" // Bolivia
        "BRL" -> "BR" // Brazil
        "BSD" -> "BS" // Bahamas
        "BTN" -> "BT" // Bhutan
        "BWP" -> "BW" // Botswana
        "BYN" -> "BY" // Belarus
        "BZD" -> "BZ" // Belize
        "CAD" -> "CA" // Canada
        "CDF" -> "CD" // DR Congo
        "CHF" -> "CH" // Switzerland
        "CLF" -> "CL" // Chile (Unidad de Fomento)
        "CLP" -> "CL" // Chile
        "CNH" -> "CN" // China (Offshore)
        "CNY" -> "CN" // China
        "COP" -> "CO" // Colombia
        "CRC" -> "CR" // Costa Rica
        "CUC" -> "CU" // Cuba (Convertible)
        "CUP" -> "CU" // Cuba
        "CVE" -> "CV" // Cape Verde
        "CZK" -> "CZ" // Czechia
        "DJF" -> "DJ" // Djibouti
        "DKK" -> "DK" // Denmark
        "DOP" -> "DO" // Dominican Republic
        "DZD" -> "DZ" // Algeria
        "EGP" -> "EG" // Egypt
        "ERN" -> "ER" // Eritrea
        "ETB" -> "ET" // Ethiopia
        "EUR" -> "EU" // European Union
        "FJD" -> "FJ" // Fiji
        "FKP" -> "FK" // Falkland Islands
        "GBP" -> "GB" // United Kingdom
        "GEL" -> "GE" // Georgia
        "GGP" -> "GG" // Guernsey
        "GHS" -> "GH" // Ghana
        "GIP" -> "GI" // Gibraltar
        "GMD" -> "GM" // Gambia
        "GNF" -> "GN" // Guinea
        "GTQ" -> "GT" // Guatemala
        "GYD" -> "GY" // Guyana
        "HKD" -> "HK" // Hong Kong
        "HNL" -> "HN" // Honduras
        "HRK" -> "HR" // Croatia
        "HTG" -> "HT" // Haiti
        "HUF" -> "HU" // Hungary
        "IDR" -> "ID" // Indonesia
        "ILS" -> "IL" // Israel
        "IMP" -> "IM" // Isle of Man
        "INR" -> "IN" // India
        "IQD" -> "IQ" // Iraq
        "IRR" -> "IR" // Iran
        "ISK" -> "IS" // Iceland
        "JEP" -> "JE" // Jersey
        "JMD" -> "JM" // Jamaica
        "JOD" -> "JO" // Jordan
        "JPY" -> "JP" // Japan
        "KES" -> "KE" // Kenya
        "KGS" -> "KG" // Kyrgyzstan
        "KHR" -> "KH" // Cambodia
        "KMF" -> "KM" // Comoros
        "KPW" -> "KP" // North Korea
        "KRW" -> "KR" // South Korea
        "KWD" -> "KW" // Kuwait
        "KYD" -> "KY" // Cayman Islands
        "KZT" -> "KZ" // Kazakhstan
        "LAK" -> "LA" // Laos
        "LBP" -> "LB" // Lebanon
        "LKR" -> "LK" // Sri Lanka
        "LRD" -> "LR" // Liberia
        "LSL" -> "LS" // Lesotho
        "LYD" -> "LY" // Libya
        "MAD" -> "MA" // Morocco
        "MDL" -> "MD" // Moldova
        "MGA" -> "MG" // Madagascar
        "MKD" -> "MK" // North Macedonia
        "MMK" -> "MM" // Myanmar
        "MNT" -> "MN" // Mongolia
        "MOP" -> "MO" // Macau
        "MRU" -> "MR" // Mauritania
        "MUR" -> "MU" // Mauritius
        "MVR" -> "MV" // Maldives
        "MWK" -> "MW" // Malawi
        "MXN" -> "MX" // Mexico
        "MYR" -> "MY" // Malaysia
        "MZN" -> "MZ" // Mozambique
        "NAD" -> "NA" // Namibia
        "NGN" -> "NG" // Nigeria
        "NIO" -> "NI" // Nicaragua
        "NOK" -> "NO" // Norway
        "NPR" -> "NP" // Nepal
        "NZD" -> "NZ" // New Zealand
        "OMR" -> "OM" // Oman
        "PAB" -> "PA" // Panama
        "PEN" -> "PE" // Peru
        "PGK" -> "PG" // Papua New Guinea
        "PHP" -> "PH" // Philippines
        "PKR" -> "PK" // Pakistan
        "PLN" -> "PL" // Poland
        "PYG" -> "PY" // Paraguay
        "QAR" -> "QA" // Qatar
        "RON" -> "RO" // Romania
        "RSD" -> "RS" // Serbia
        "RUB" -> "RU" // Russia
        "RWF" -> "RW" // Rwanda
        "SAR" -> "SA" // Saudi Arabia
        "SBD" -> "SB" // Solomon Islands
        "SCR" -> "SC" // Seychelles
        "SDG" -> "SD" // Sudan
        "SEK" -> "SE" // Sweden
        "SGD" -> "SG" // Singapore
        "SHP" -> "SH" // Saint Helena
        "SLE" -> "SL" // Sierra Leone
        "SLL" -> "SL" // Sierra Leone (Old)
        "SOS" -> "SO" // Somalia
        "SRD" -> "SR" // Suriname
        "SSP" -> "SS" // South Sudan
        "STD" -> "ST" // São Tomé and Príncipe (Old)
        "STN" -> "ST" // São Tomé and Príncipe
        "SVC" -> "SV" // El Salvador
        "SYP" -> "SY" // Syria
        "SZL" -> "SZ" // Eswatini
        "THB" -> "TH" // Thailand
        "TJS" -> "TJ" // Tajikistan
        "TMT" -> "TM" // Turkmenistan
        "TND" -> "TN" // Tunisia
        "TOP" -> "TO" // Tonga
        "TRY" -> "TR" // Turkey
        "TTD" -> "TT" // Trinidad and Tobago
        "TWD" -> "TW" // Taiwan
        "TZS" -> "TZ" // Tanzania
        "UAH" -> "UA" // Ukraine
        "UGX" -> "UG" // Uganda
        "USD" -> "US" // United States
        "UYU" -> "UY" // Uruguay
        "UZS" -> "UZ" // Uzbekistan
        "VES" -> "VE" // Venezuela
        "VND" -> "VN" // Vietnam
        "VUV" -> "VU" // Vanuatu
        "WST" -> "WS" // Samoa
        "XAF" -> "CM" // Central African CFA -> Cameroon (Representative)
        "XCD" -> "AG" // East Caribbean Dollar -> Antigua and Barbuda (Representative)
        "XCG" -> "CW" // Caribbean Guilder -> Curaçao
        "XOF" -> "CI" // West African CFA -> Côte d'Ivoire (Representative)
        "XPF" -> "PF" // CFP Franc -> French Polynesia
        "YER" -> "YE" // Yemen
        "ZAR" -> "ZA" // South Africa
        "ZMW" -> "ZM" // Zambia
        "ZWG" -> "ZW" // Zimbabwe Gold
        "ZWL" -> "ZW" // Zimbabwe

        // Commodities & Crypto (No Country)
        "BTC", "XAG", "XAU", "XDR", "XPD", "XPT" -> null

        else -> null
    }?.lowercase()
}
