import { test, expect } from '@playwright/test'

test.describe('前台博客冒烟', () => {
  test('首页可访问', async ({ page }) => {
    await page.goto('/')
    await expect(page).toHaveTitle(/博客/)
    await expect(page.locator('.nblog-site')).toBeVisible()
  })

  test('关于页可访问并含联系方式', async ({ page }) => {
    await page.goto('/about')
    await expect(page.getByText('关于我').first()).toBeVisible()
    await expect(page.getByText('联系方式')).toBeVisible()
    await expect(page.getByText('GitHub')).toBeVisible()
  })

  test('友人帐页可访问', async ({ page }) => {
    await page.goto('/friends')
    await expect(page.getByText('友人帐').first()).toBeVisible()
  })

  test('归档页可访问', async ({ page }) => {
    await page.goto('/archives')
    await expect(page.getByText('文章归档').first()).toBeVisible()
  })

  test('标签页可访问', async ({ page }) => {
    await page.goto('/tag')
    await expect(page.getByText('标签云').first()).toBeVisible()
  })

  test('顶栏导航切换正常', async ({ page }) => {
    await page.goto('/')
    await page.getByRole('link', { name: '关于我' }).click()
    await expect(page).toHaveURL(/\/about/)
    await page.getByRole('link', { name: '归档' }).click()
    await expect(page).toHaveURL(/\/archives/)
  })

  test('侧边栏标签云存在', async ({ page }) => {
    await page.goto('/')
    await expect(page.locator('.nblog-tag-cloud-widget')).toBeVisible()
  })
})
