import {defineVersionedConfig} from "@viteplus/versions";

// https://vitepress.dev/reference/site-config
export default defineVersionedConfig({
    title: "Modolus framework",
    description: "Documentation of the modolus framework",
    head: [[
        'link', {rel: 'icon', href: '/modolus-framework-no-text.svg'}
    ]],

    versionsConfig: {
        current: 'latest',
        sources: 'src',
        archive: 'versions',
        versionSwitcher: {
            text: 'Version',
            includeCurrentVersion: true
        }
    },

    themeConfig: {
        logo: '/modolus-framework-no-text.svg',
        // https://vitepress.dev/reference/default-theme-config
        nav: [
            {text: 'Home', link: '/'},
            {text: 'Guide', link: '/guide/getting-started'}
        ],

        sidebar: [
            {
                text: 'Introduction',
                items: [
                    {text: 'Getting started', link: '/guide/getting-started'}
                ]
            }
        ],

        socialLinks: [
            {icon: 'github', link: 'https://github.com/Modolus-Framework/modolus'}
        ]
    }
})
