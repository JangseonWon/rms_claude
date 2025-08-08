/** @type {import('next').NextConfig} */
const nextConfig = {
    reactStrictMode: false,
    async rewrites() {
        const baseUrl = process.env.BASE_URL
        return [
            {
                source: '/w-api/login-service/:path*',
                destination: `${baseUrl}/w-api/login-service/:path*`,
            },
            {
                source: '/w-api/management-service/:path*',
                destination: `${baseUrl}/w-api/management-service/:path*`,
            },
            {
                source: '/w-api/order-service/:path*',
                destination: `${baseUrl}/w-api/order-service/:path*`,
            },
            {
                source: '/w-api/dashboard-service/:path*',
                destination: `${baseUrl}/w-api/dashboard-service/:path*`,
            },
            {
                source: '/w-api/cart-service/:path*',
                destination: `${baseUrl}/w-api/cart-service/:path*`,
            },
            {
                source: '/w-api/post-service/:path*',
                destination: `${baseUrl}/w-api/post-service/:path*`,
            },
            {
                source: '/w-api/profile-service/:path*',
                destination: `${baseUrl}/w-api/profile-service/:path*`,
            },
            {
                source: '/w-api/catalog-service/:path*',
                destination: `${baseUrl}/w-api/catalog-service/:path*`,
            },
            {
                source: '/w-api/result-service/:path*',
                destination: `${baseUrl}/w-api/result-service/:path*`,
            },
            {
                source: '/w-api/home-service/:path*',
                destination: `${baseUrl}/w-api/home-service/:path*`,
            }
        ];
    },
    output: 'standalone',
};

export default nextConfig;