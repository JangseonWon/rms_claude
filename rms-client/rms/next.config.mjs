/** @type {import('next').NextConfig} */
const nextConfig = {
    reactStrictMode: false,
    async rewrites() {
        return [
            {
                source: '/w-api/login-service/:path*',
                destination: `https://rms-test.gcgenome.com/w-api/login-service/:path*`,
            },
            {
                source: '/w-api/product-service/:path*',
                destination: `https://rms-test.gcgenome.com/w-api/product-service/:path*`,
            },
            {
                source: '/w-api/management-service/:path*',
                destination: `https://rms-test.gcgenome.com/w-api/management-service/:path*`,
            },
            {
                source: '/w-api/organization-service/:path*',
                destination: `https://rms-test.gcgenome.com/w-api/organization-service/:path*`,
            },
            {
                source: '/w-api/order-service/:path*',
                destination: `https://rms-test.gcgenome.com/w-api/order-service/:path*`,
                // destination: `http://localhost:9886/w-api/order-service/:path*`,
            },
            {
                source: '/w-api/dashboard-service/:path*',
                destination: `https://rms-test.gcgenome.com/w-api/dashboard-service/:path*`,
            },
            {
                source: '/w-api/cart-service/:path*',
                // destination: `https://rms-test.gcgenome.com/w-api/cart-service/:path*`,
                destination: `http://localhost:9886/w-api/cart-service/:path*`,
            },
            {
                source: '/w-api/post-service/:path*',
                destination: `https://rms-test.gcgenome.com/w-api/post-service/:path*`,
            },
            {
                source: '/w-api/profile-service/:path*',
                destination: `https://rms-test.gcgenome.com/w-api/profile-service/:path*`,
            },
            {
                source: '/w-api/catalog-service/:path*',
                // destination: `https://rms-test.gcgenome.com/w-api/catalog-service/:path*`,
                destination: `http://localhost:9098/w-api/catalog-service/:path*`,
            },
            {
                source: '/w-api/result-service/:path*',
                destination: `https://rms-test.gcgenome.com/w-api/result-service/:path*`,
            }
        ];
    },
    output: 'standalone',
};

export default nextConfig;