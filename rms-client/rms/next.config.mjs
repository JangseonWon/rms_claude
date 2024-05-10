/** @type {import('next').NextConfig} */
const nextConfig = {
    async rewrites() {
        return [
            {
                source: '/w-api/login-service/:path*',
                destination: `https://rms-test.gcgenome.com/w-api/login-service/:path*`,
            },
            {
                source: '/w-api/management-service/:path*',
                destination: `http://localhost:9090/w-api/management-service/:path*`,
            },
            {
                source: '/w-api/product-service/:path*',
                destination: `http://localhost:9887/w-api/product-service/:path*`,
            },
            {
                source: '/w-api/dashboard-service/:path*',
                destination: `https://rms-test.gcgenome.com/w-api/dashboard-service/:path*`,
            },
        ]
    },
};
export default nextConfig;
