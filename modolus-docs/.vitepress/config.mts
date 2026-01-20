import {defineConfig} from 'vitepress'

// https://vitepress.dev/reference/site-config
export default defineConfig({
    title: "Modolus framework",
    description: "Documentation of the modolus framework",
    head: [[
        'link', { rel: 'icon', href: '/modolus-framework-no-text.svg'}
    ]],
    themeConfig: {
        logo: '/modolus-framework-no-text.svg',
        // https://vitepress.dev/reference/default-theme-config
        nav: [
            {text: 'Home', link: '/'},
            {text: 'Examples', link: '/markdown-examples'}
        ],

        sidebar: [
            {
                text: 'Examples',
                items: [
                    {text: 'Markdown Examples', link: '/markdown-examples'},
                    {text: 'Runtime API Examples', link: '/api-examples'}
                ]
            }
        ],

        socialLinks: [
            {icon: 'github', link: 'https://github.com/Modolus-Framework/modolus'}
        ]
    }
})
